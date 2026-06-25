package it.itsacademy.gestione_ordini.client;

import it.itsacademy.gestione_ordini.dto.PaymentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentPublisherAMQP {

    private final RabbitTemplate rabbitTemplate;

    public void publishPaymentRequest(PaymentRequestDTO requestDTO) {
        // Envoi vers l'exchange avec la clé dédiée à la file de traitement bancaire
        rabbitTemplate.convertAndSend("orders.exchange", "order.routing.payment", requestDTO);
        System.out.println("[8080] Oggetto PaymentRequestDTO inviato verso la coda pagamenti.");
    }
}