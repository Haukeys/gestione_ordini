package it.itsacademy.gestione_ordini.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Ordine")
@Audited
@AuditOverride(forClass = Auditable.class)
public class Ordine extends Auditable {

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
