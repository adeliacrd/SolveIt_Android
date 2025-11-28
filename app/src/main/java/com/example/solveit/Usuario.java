package com.example.solveit;

public class Usuario {
    private String nome;
    private String email;
    private String telefone;
    private String empresa;
    private boolean isAtivo;

    public Usuario(String nome, String email, String telefone, String empresa, boolean isAtivo) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.empresa = empresa;
        this.isAtivo = isAtivo;
    }

    // Getters
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getEmpresa() { return empresa; }
    public boolean isAtivo() { return isAtivo; }

    // Setter
    public void setAtivo(boolean ativo) { isAtivo = ativo; }
}
