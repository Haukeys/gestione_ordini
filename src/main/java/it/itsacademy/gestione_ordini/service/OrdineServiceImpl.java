package it.itsacademy.gestione_ordini.service;


import it.itsacademy.gestione_ordini.client.PaymentServiceClient;
import it.itsacademy.gestione_ordini.dto.OrdineCreateDTO;
import it.itsacademy.gestione_ordini.dto.OrdineResponseDTO;
import it.itsacademy.gestione_ordini.dto.PaymentRequestDTO;
import it.itsacademy.gestione_ordini.dto.PaymentResponseDTO;
import it.itsacademy.gestione_ordini.entity.Ordine;
import it.itsacademy.gestione_ordini.entity.TipoOrdine;
import it.itsacademy.gestione_ordini.mapper.OrdineMapper;
import it.itsacademy.gestione_ordini.repository.OrdineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


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
}


