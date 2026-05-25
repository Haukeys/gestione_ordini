package it.itsacademy.gestione_ordini.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Ordine")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idOrdine;

    @Column(nullable = false)
    private LocalDate dataCreazione;

    @Column(nullable = false)
    private String descrizione;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoOrdine statoOrdine;

    @Column(nullable = false)
    private Double totale;

}
