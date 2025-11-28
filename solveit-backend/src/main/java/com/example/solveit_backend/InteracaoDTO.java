package com.example.solveit_backend;

// Molde para uma única mensagem na timeline
public class InteracaoDTO {
    private int id_interacoes;
    private int id_chamado;
    private int id_usuario;
    private String nome_usuario;
    private String mensagem;
    private String dt_interacao;

    // Getters para o Gson funcionar
    public int getId_interacoes() { return id_interacoes; }
    public int getId_chamado() { return id_chamado; }
    public int getId_usuario() { return id_usuario; }
    public String getNome_usuario() { return nome_usuario; }
    public String getMensagem() { return mensagem; }
    public String getDt_interacao() { return dt_interacao; }
}
