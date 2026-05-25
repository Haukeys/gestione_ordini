package it.itsacademy.gestione_ordini.service;

import it.itsacademy.gestione_ordini.dto.OrdineCreateDTO;
import it.itsacademy.gestione_ordini.dto.OrdineDTO;
import it.itsacademy.gestione_ordini.dto.OrdineResponseDTO;
import it.itsacademy.gestione_ordini.entity.Ordine;

import java.util.UUID;

public interface OrdineService {

    public Ordine createOrdine(OrdineCreateDTO ordinecreateDTO);
    public OrdineResponseDTO updateOrdine(UUID idOrdine);

}
