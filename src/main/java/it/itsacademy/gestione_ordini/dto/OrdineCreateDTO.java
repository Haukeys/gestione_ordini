package it.itsacademy.gestione_ordini.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;

@Data
public class OrdineCreateDTO {//per la creazione del ordine senza datacreazione perche settato in service


    @NotBlank(message = "descrizione non puo essere vuota")
    private String descrizione;

    @NotNull(message = "totale non puo essere null")
    @Positive(message = "il totale deve essere maggiore di zero")
    private Double totale;
}
