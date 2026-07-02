package it.itsacademy.gestione_ordini.service;


import it.itsacademy.gestione_ordini.client.PaymentPublisherAMQP;
import it.itsacademy.gestione_ordini.client.PaymentServiceClient;
import it.itsacademy.gestione_ordini.dto.*;
import it.itsacademy.gestione_ordini.entity.Ordine;
import it.itsacademy.gestione_ordini.entity.TipoOrdine;
import it.itsacademy.gestione_ordini.mapper.OrdineMapper;
import it.itsacademy.gestione_ordini.repository.OrdineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrdineServiceImpl implements OrdineService {

    private final OrdineRepository ordineRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final OrdineMapper ordineMapper;
    private final PaymentPublisherAMQP paymentPublisherAMQP;

    @Override
    @Transactional
    public Ordine createOrdine(OrdineCreateDTO ordineCreateDTO) {

        //Conversione passiva via MapStruct
        Ordine ordine = ordineMapper.toOrdine(ordineCreateDTO);
        //settiamo la data
        ordine.setDataCreazione(LocalDate.now());
        // settiamo l'ordine nello stato giusto initiale (DAPAGARE)
        ordine.setStatoOrdine(TipoOrdine.DAPAGARE);

        // registrazione in db
        return ordineRepository.save(ordine);
    }

    // 1. MÉTHODE ASYNCHRONE : Envoi de la commande à RabbitMQ
    @Override
    @Transactional
    public void inviaPagamentoOrdine(UUID idOrdine, UUID idUtente) {//on ajoute idUtente pour que le cycle avec la traçabiliter des emails soit possible entre les 3 acteurs
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordine non trovato"));

        // Passe la commande à l'état transitoire
        ordine.setStatoOrdine(TipoOrdine.IN_ELABORAZIONE);
        ordineRepository.save(ordine);

        // Envoi du DTO via Jackson dans RabbitMQ
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setIdOrdine(idOrdine);
        requestDTO.setIdUtente(idUtente);//ajout pour le teste avec l'email
        requestDTO.setDescrizione(ordine.getDescrizione());//ajout personnel pour avoir la description du produit
        requestDTO.setTotale(ordine.getTotale());//ajout pour test docker
        paymentPublisherAMQP.publishPaymentRequest(requestDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdineResponseDTO> getAllOrders() {
        return ordineRepository.findAll()
                .stream()
                .map(ordineMapper::toOrdineResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET INFO ORDER (BY ID)
     * Récupère une commande spécifique par son UUID.
     * Si l'ID n'existe pas, lève une exception (Spring renverra un code 404).
     */

    @Override
    @Transactional
    public OrdineResponseDTO getOrderInfo(UUID idOrdine) {
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordine non trovato"));

        // Si l'ordine est dans un état transitoire, on synchronise le statut avec gestione_pagamento
        if (ordine.getStatoOrdine() == TipoOrdine.IN_ELABORAZIONE) {
            PaymentResponseDTO paymentResponse = paymentServiceClient.getPaymentStatusByOrdineId(idOrdine);
            if (paymentResponse != null) {
                if ("ACCETTATO".equalsIgnoreCase(paymentResponse.getStatoPagamento())) {
                    ordine.setStatoOrdine(TipoOrdine.PAGATO);
                } else if ("RIFIUTATO".equalsIgnoreCase(paymentResponse.getStatoPagamento())) {
                    ordine.setStatoOrdine(TipoOrdine.DAPAGARE);
                }
                ordineRepository.save(ordine);
            }
        }

        return ordineMapper.toOrdineResponseDTO(ordine);
    }

    /**
     * LOGICAL DELETE (Soft Delete)
     * Au lieu de faire un repository.delete(), on passe le statut à ELIMINATO.
     * Une commande supprimée logiquement reste en BDD pour l'historique.
     */

    @Override
    @Transactional
    public OrdineResponseDTO logicalDeleteOrder(UUID idOrdine) {
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordine non trovato"));

        // Règle métier : On applique le statut ELIMINATO
        ordine.setStatoOrdine(TipoOrdine.ELIMINATO);

        // Sauvegarde de la modification
        ordine = ordineRepository.save(ordine);
        return ordineMapper.toOrdineResponseDTO(ordine);
    }
    @Override
    @Transactional(readOnly = true)
    public List<PagamentoHistoryDTO> getOrdinePagamentiLista(UUID idOrdine) {
        // Optionnel : Sécurité pour vérifier que l'ordre existe chez nous
        if (!ordineRepository.existsById(idOrdine)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordine non trovato");
        }

        // Appel du microservice paiement via le RestClient
        return paymentServiceClient.getPaymentsHistory(idOrdine);
    }
    @Override
    @Transactional
    public OrdineDTO getInfoPagamento(UUID idOrdine) {
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordine non trovato"));

        if (ordine.getStatoOrdine() == TipoOrdine.IN_ELABORAZIONE) {
            PaymentResponseDTO paymentResponse = paymentServiceClient.getPaymentStatusByOrdineId(idOrdine);

            if (paymentResponse != null) {
                if ("ACCETTATO".equalsIgnoreCase(paymentResponse.getStatoPagamento())) {
                    ordine.setStatoOrdine(TipoOrdine.PAGATO);
                } else if ("RIFIUTATO".equalsIgnoreCase(paymentResponse.getStatoPagamento())) {
                    // CORRECTION : Remettre DAPAGARE au lieu de laisser bloqué en IN_ELABORAZIONE
                    ordine.setStatoOrdine(TipoOrdine.DAPAGARE);
                }
                ordineRepository.save(ordine);
            }
        }
        return ordineMapper.toOrdineDTO(ordine);
    }


}

