package it.itsacademy.gestione_ordini.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        log.error("[SERVICE ERROR] Ordini - Erreur HTTP : {} - {}", ex.getStatusCode(), ex.getReason());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        log.error("[SERVICE ERROR] Ordini - Unexpected Crash : ", ex);
        return new ResponseEntity<>("Internal error occur during order", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}