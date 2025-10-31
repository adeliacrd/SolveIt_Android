package com.example.solveit.api; // Use seu pacote

public class AbrirChamadoResponse {
    private boolean success;
    private String message;
    private Integer id_chamado; // O ID do chamado recém-criado

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Integer getId_chamado() { return id_chamado; }
}