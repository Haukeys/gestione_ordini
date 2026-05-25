package it.itsacademy.gestione_ordini.client;


import it.itsacademy.gestione_ordini.dto.PaymentRequestDTO;
import it.itsacademy.gestione_ordini.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PaymentServiceClient {

    private final RestClient paymentRestClient;

    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
            try {
                return paymentRestClient.post()
                        .uri("/payments")
                        .body(paymentRequest)
                        .retrieve()
                        .body(PaymentResponseDTO.class);
            } catch (Exception e) {
                System.err.println("Errore di comunicazione con il servizio pagamenti: " + e.getMessage());
                return null; // Ritorna null in caso di crash o timeout del servizio esterno
            }
    }

}
