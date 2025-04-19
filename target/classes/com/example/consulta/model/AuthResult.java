package com.example.consulta.model;

public class AuthResult {
    private Usuario usuario;
    private String error;

    public AuthResult(Usuario usuario) {
        this.usuario = usuario;
    }

    public AuthResult(String error) {
        this.error = error;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return usuario != null;
    }
} 