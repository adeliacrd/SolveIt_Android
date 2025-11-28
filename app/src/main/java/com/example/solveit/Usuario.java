package com.example.solveit;

public class Usuario {
    private String nome;
    private String email;
    private String telefone;
    private String empresa;
    private boolean isAtivo;

    // ✅ NOVO: Campo para o tipo de acesso do usuário (ex: "Administrador", "Agente", "Usuário")
    private String tipoDeAcesso;

    // ✅ NOVO: Campo para controlar o estado de expansão do item na lista
    private boolean expandido;

    /**
     * Construtor atualizado para incluir o tipo de acesso.
     * O estado 'expandido' é inicializado como 'false' por padrão.
     */
    public Usuario(String nome, String email, String telefone, String empresa, boolean isAtivo, String tipoDeAcesso) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.empresa = empresa;
        this.isAtivo = isAtivo;
        this.tipoDeAcesso = tipoDeAcesso;
        this.expandido = false; // Todo usuário começa com o item recolhido
    }

    // --- Getters ---
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getEmpresa() { return empresa; }
    public boolean isAtivo() { return isAtivo; }

    // ✅ NOVO
    public String getTipoDeAcesso() { return tipoDeAcesso; }

    // ✅ NOVO
    public boolean isExpandido() { return expandido; }


    // --- Setters ---
    public void setAtivo(boolean ativo) { isAtivo = ativo; }

    // ✅ NOVO
    public void setTipoDeAcesso(String tipoDeAcesso) { this.tipoDeAcesso = tipoDeAcesso; }

    // ✅ NOVO
    public void setExpandido(boolean expandido) { this.expandido = expandido; }
}
