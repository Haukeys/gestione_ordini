package it.itsacademy.gestione_ordini.dto;

import it.itsacademy.gestione_ordini.entity.TipoOrdine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrdineDTO {

    private UUID idOrdine;


    @NotNull(message = "data di creazione non puo essere null")
    private LocalDate dataCreazione;

    @NotBlank(message = "descrizione non puo essere vuoto")
    @NotNull(message = "descrizione non puo essere vuoto")
    private String descrizione;


    @NotNull(message = "statoOrdine non puo essere vuoto")
    private TipoOrdine statoOrdine;


    @NotNull(message = "totale non puo essere vuoto")
    @Positive(message = "totale deve essere positivo")
    private Double totale;
}