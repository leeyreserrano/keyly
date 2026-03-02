package com.keyly.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Classe que recull totes les excepcions que puguin sortir
 */
@RestControllerAdvice
public class GestorGlobalExcepcions {

    @ExceptionHandler(EntitatNoTrobadaException.class)
    public ResponseEntity<String> noTrobat(EntitatNoTrobadaException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(CorreuExistentException.class)
    public ResponseEntity<String> correuExistent(CorreuExistentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

}
