package com.example.consulta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConsultaNotFoundException extends RuntimeException {
    
    public ConsultaNotFoundException(String message) {
        super(message);
    }
    
    public ConsultaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 