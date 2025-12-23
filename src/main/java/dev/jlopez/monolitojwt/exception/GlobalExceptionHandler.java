package dev.jlopez.monolitojwt.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.jlopez.monolitojwt.common.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Usuario no encontrado(404)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(UserNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    //2. handleGLobalException para evitar errores no manejados en la app. 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex){
        return buildError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocurrio un error interno inesperado");
    }


    //funcion transversal para construccion de errores
    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String message){
        ErrorResponse error = new ErrorResponse(
            status.value(),
            message,
            LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(error);
    }
}
