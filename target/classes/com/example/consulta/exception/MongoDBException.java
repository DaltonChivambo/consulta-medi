package com.example.consulta.exception;

public class MongoDBException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public MongoDBException(String message) {
        super(message);
    }
    
    public MongoDBException(String message, Throwable cause) {
        super(message, cause);
    }
} 