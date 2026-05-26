package it.itsacademy.gestione_ordini.service;

import it.itsacademy.gestione_ordini.dto.OrdineCreateDTO;
import it.itsacademy.gestione_ordini.dto.OrdineDTO;
import it.itsacademy.gestione_ordini.dto.OrdineResponseDTO;
import it.itsacademy.gestione_ordini.dto.PagamentoHistoryDTO;
import it.itsacademy.gestione_ordini.entity.Ordine;

import java.util.List;
import java.util.UUID;

public interface OrdineService {

    public Ordine createOrdine(OrdineCreateDTO ordinecreateDTO);
    public OrdineResponseDTO updateOrdine(UUID idOrdine);
    public List<OrdineResponseDTO> getAllOrders();
    public OrdineResponseDTO getOrderInfo(UUID idOrdine);
    public OrdineResponseDTO logicalDeleteOrder(UUID idOrdine);
    public List<PagamentoHistoryDTO> getOrdinePagamentiLista(UUID idOrdine);
}
