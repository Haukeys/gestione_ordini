package it.itsacademy.gestione_ordini.controller;



import it.itsacademy.gestione_ordini.dto.OrdineCreateDTO;
import it.itsacademy.gestione_ordini.dto.OrdineDTO;
import it.itsacademy.gestione_ordini.dto.OrdineResponseDTO;
import it.itsacademy.gestione_ordini.dto.PagamentoHistoryDTO;
import it.itsacademy.gestione_ordini.entity.Ordine;
import it.itsacademy.gestione_ordini.service.OrdineServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

// C'est cette méthode que tu vas appeler avec Postman
    @PostMapping("/{id}/paga")
    public ResponseEntity<String> inviaPagamento(@PathVariable UUID id) {

        // On appelle le service qui contient notre PaymentPublisherAMQP (sans Jackson !)
        ordineService.inviaPagamentoOrdine(id);

        // On répond immédiatement au client
        return ResponseEntity.ok("Richiesta di pagamento mandato a RabbitMQ con successo !");
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
    @GetMapping("/{id}/infoPagamenti")
    public ResponseEntity<OrdineDTO> getInfoPagamenti(@PathVariable UUID id) {
        // Ici, on appelle enfin la méthode de ton service !
        OrdineDTO ordineDTO = ordineService.getInfoPagamento(id);

        return ResponseEntity.ok(ordineDTO);
    }
}
