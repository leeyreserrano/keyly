package com.keyly.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Classe que recull totes les excepcions que puguin sortir
 */
@RestControllerAdvice
public class GestorGlobalExcepcions {

    @ExceptionHandler(EntitatNoTrobadaException.class)
    public ResponseEntity<ErrorResponse> noTrobat(EntitatNoTrobadaException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CorreuExistentException.class)
    public ResponseEntity<ErrorResponse> correuExistent(CorreuExistentException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DominiInvalidException.class)
    public ResponseEntity<ErrorResponse> dominiError(DominiInvalidException e) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorResponse e = new ErrorResponse(400, "Correu no válid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

}
