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

    @NotNull(message = "idOrdine non puo essere null")
    @NotBlank(message = "idOrdine non puo essere vuoto")
    private UUID idOrdine;

    @NotBlank(message = "totale non puo essere vuoto")
    @NotNull(message = "totale non puo essere null")
    @Positive(message = "totale deve essere positivo")
    private Double totale;
}
