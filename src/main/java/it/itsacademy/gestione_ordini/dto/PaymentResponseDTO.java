package it.itsacademy.gestione_ordini.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {

    private UUID idPagamento;  // ID del pagamento generato da gestione_pagamento

    private UUID idOrdine;     // ID del ordine recepito

    // Riceviamo lo status sotto forma di string per evitare di clonarlo
    // l'enum "TipoPagamento" di gestione_pagamento

    private String statoPagamento;
}