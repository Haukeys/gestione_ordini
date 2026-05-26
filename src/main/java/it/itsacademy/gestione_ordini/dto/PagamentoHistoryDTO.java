package it.itsacademy.gestione_ordini.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PagamentoHistoryDTO {
    private UUID idPagamento;
    private String statoPagamento; // Reçu comme String depuis le JSON
    private LocalDate dataPagamento;
}