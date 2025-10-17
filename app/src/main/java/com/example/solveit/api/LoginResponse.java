package com.example.solveit.api; // USE SEU PACOTE CORRETO

// Esta classe mapeia o JSON de sucesso/falha do login
public class LoginResponse {
    private boolean success;
    private String message;
    private Integer id_acesso;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Integer getIdAcesso() { return id_acesso; }
}