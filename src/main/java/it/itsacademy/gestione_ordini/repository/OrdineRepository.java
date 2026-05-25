package it.itsacademy.gestione_ordini.repository;

import it.itsacademy.gestione_ordini.entity.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrdineRepository extends JpaRepository<Ordine, UUID> {

}
