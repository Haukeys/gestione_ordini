package it.itsacademy.gestione_ordini.client;


import it.itsacademy.gestione_ordini.dto.PagamentoHistoryDTO;
import it.itsacademy.gestione_ordini.dto.PaymentRequestDTO;
import it.itsacademy.gestione_ordini.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentServiceClient {//considere cette classe comme etant un service reel mais avec une structure speciale

    private final RestClient paymentRestClient;

    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
            try {
                return paymentRestClient.post()
                        .uri("/pagamenti")//qua deve andare esattamente quello che c'è sul controller del microservizio con qui deve comunicare(@RequestMapping)
                        .body(paymentRequest)
                        .retrieve()
                        .body(PaymentResponseDTO.class);
            } catch (Exception e) {
                System.err.println("Errore di comunicazione con il servizio pagamenti: " + e.getMessage());
                return null; // Ritorna null in caso di crash o timeout del servizio esterno
            }
    }
    public List<PagamentoHistoryDTO> getPaymentsHistory(UUID idOrdine) {
        try {
            return paymentRestClient.get()
                    .uri("/pagamenti/ordine/{idOrdine}", idOrdine)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<PagamentoHistoryDTO>>() {});
        } catch (Exception e) {
            // En cas d'erreur ou si le service 8081 est down, on renvoie une liste vide (ou on lève une exception)
            return Collections.emptyList();
        }
    }
    public PaymentResponseDTO getPaymentStatusByOrdineId(UUID idOrdine) {
        try {
            // CORRECTION : URI alignée sur le controleur de l'autre microservice et type unique (pas de tableau)
            return paymentRestClient.get()
                    .uri("/pagamenti/ordine/{idOrdine}/status", idOrdine)
                    .retrieve()
                    .body(PaymentResponseDTO.class);
        } catch (Exception e) {
            System.err.println("Errore di comunicazione per lo stato pagamento: " + e.getMessage());
            return null;
        }
    }}
