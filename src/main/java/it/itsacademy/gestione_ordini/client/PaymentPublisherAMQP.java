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
        // Envoie l'objet complet converti automatiquement en JSON par Jackson
        rabbitTemplate.convertAndSend("exam_exchange", "exam_routingKey", requestDTO);
        System.out.println("[8080] Oggetto PaymentRequestDTO inviato come JSON.");
    }
}