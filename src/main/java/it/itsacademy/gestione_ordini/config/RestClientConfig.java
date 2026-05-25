package it.itsacademy.gestione_ordini.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${payment.service.url:http://localhost:8082}") // URL par défaut si non définie dans application.properties
    private String paymentServiceUrl;

    @Bean
    public RestClient paymentRestClient() {
        return RestClient.builder()
                .baseUrl(paymentServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}


