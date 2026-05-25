package it.itsacademy.gestione_ordini.dto;

import it.itsacademy.gestione_ordini.entity.TipoOrdine;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class OrdineResponseDTO {
    private UUID idOrdine;
    private LocalDate dataCreazione;
    private String descrizione;
    private TipoOrdine statoOrdine;
    private Double totale;
}
