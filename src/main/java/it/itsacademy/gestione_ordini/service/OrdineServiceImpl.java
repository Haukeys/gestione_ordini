package it.itsacademy.gestione_ordini.service;


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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrdineServiceImpl implements OrdineService {

    private final OrdineRepository ordineRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final OrdineMapper ordineMapper;


    @Override
    @Transactional
    public Ordine createOrdine(OrdineCreateDTO ordineCreateDTO) {

        //Conversione passiva via MapStruct
        Ordine ordine = ordineMapper.toOrdine(ordineCreateDTO);

        // settiamo l'ordine nello stato giusto initiale (DAPAGARE)
        ordine.setStatoOrdine(TipoOrdine.DAPAGARE);

        // registrazione in db
        return ordineRepository.save(ordine);
    }


    @Override
    public OrdineResponseDTO updateOrdine(UUID idOrdine) {

        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato con id: " + idOrdine));

        // Appel vers le microservice externe (8081)
        PaymentRequestDTO paymentRequest = new PaymentRequestDTO(ordine.getIdOrdine(), ordine.getTotale());
        PaymentResponseDTO paymentResponse = paymentServiceClient.processPayment(paymentRequest);

        // Mise à jour de l'état selon la réponse
        if (paymentResponse != null && "ACCETTATO".equals(paymentResponse.getStatoPagamento())) {
            ordine.setStatoOrdine(TipoOrdine.PAGATO);
        } else {
            ordine.setStatoOrdine(TipoOrdine.DAPAGARE);
        }

        // Sauvegarde de l'état final (PAGATO ou DAPAGARE)
        ordine = ordineRepository.save(ordine);
        return ordineMapper.toOrdineResponseDTO(ordine);
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
    @Transactional(readOnly = true)
    public OrdineResponseDTO getOrderInfo(UUID idOrdine) {
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordine non trovato"));
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

}


