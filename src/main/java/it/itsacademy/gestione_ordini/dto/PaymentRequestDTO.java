package it.itsacademy.gestione_ordini.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {//


    private UUID idOrdine;

    //ajout pour la gestion de l'email
    private UUID idUtente;

    //@NotBlank(message = "totale non puo essere vuoto")essaye pour docker
    @NotNull(message = "totale non puo essere null")
    @Positive(message = "totale deve essere positivo")
    private Double totale;

    // Champs de transit pour les queues asynchrones
    private UUID idPagamento;
    private String nomeRicevuta;
    private String descrizione;
}
