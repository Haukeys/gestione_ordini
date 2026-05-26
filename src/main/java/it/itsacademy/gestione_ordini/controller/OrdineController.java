package it.itsacademy.gestione_ordini.controller;



import it.itsacademy.gestione_ordini.dto.OrdineCreateDTO;
import it.itsacademy.gestione_ordini.dto.OrdineResponseDTO;
import it.itsacademy.gestione_ordini.dto.PagamentoHistoryDTO;
import it.itsacademy.gestione_ordini.entity.Ordine;
import it.itsacademy.gestione_ordini.entity.TipoOrdine;
import it.itsacademy.gestione_ordini.service.OrdineServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ordini")
@RequiredArgsConstructor
public class OrdineController {

    private final OrdineServiceImpl ordineService;

    /**
     * CREZIONE DEL ORDINE
     * Riceve il JSON, verifica les constraints (@Valid)
     * e registra l'ordine nello stato initiale (DAPAGARE)
     */

    @PostMapping
    public ResponseEntity<Ordine> createOrder(@Valid @RequestBody OrdineCreateDTO ordineDTO) {
        Ordine createdOrder = ordineService.createOrdine(ordineDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    /**
     * IINIZIALIZATIONE ET VERIFICA DEL PAGAMENTO
     * Riceve l'id del ordine(UUID) gia esistente via l'URL,
     * chiama il microservizio di pagamento (8081) tramite RestClient
     * e aggiorna lo stato del ordine a secondo della response(PAGATO o DAPAGARE)
     */

    @PutMapping(path = "/{id}/paga")
    public ResponseEntity<OrdineResponseDTO> payOrder(@Valid @PathVariable("id") UUID idOrdine) {

        OrdineResponseDTO response = ordineService.updateOrdine(idOrdine);

        //Si le paiement est validé, on renvoie un statut 200 OK
        if (response.getStatoOrdine() == TipoOrdine.PAGATO) {
            return ResponseEntity.ok(response);
        }

        //Si le paiement a échoué (DAPAGARE), le contrôleur lève manuellement une exception 402
        throw new ResponseStatusException(
                HttpStatus.PAYMENT_REQUIRED,
                "Il pagamento ha fallito. L'ordine rimane in stato DAPAGARE."
        );
    }

    @GetMapping
    public ResponseEntity<List<OrdineResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(ordineService.getAllOrders());
    }


    @GetMapping(path = "/{id}")
    public ResponseEntity<OrdineResponseDTO> getOrderInfo(@Valid @PathVariable("id") UUID idOrdine) {
        return ResponseEntity.ok(ordineService.getOrderInfo(idOrdine));
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity<OrdineResponseDTO> deleteOrder(@Valid @PathVariable("id") UUID idOrdine) {
        // Torna l'oggetto aggiornato con statoOrdine "ELIMINATO"
        return ResponseEntity.ok(ordineService.logicalDeleteOrder(idOrdine));
    }

    @GetMapping("/{idOrdine}/pagamenti")
    public ResponseEntity<List<PagamentoHistoryDTO>> getListaPagamentiOrdine(@PathVariable UUID idOrdine) {
        List<PagamentoHistoryDTO> lista = ordineService.getOrdinePagamentiLista(idOrdine);
        return ResponseEntity.ok(lista);
    }
}
