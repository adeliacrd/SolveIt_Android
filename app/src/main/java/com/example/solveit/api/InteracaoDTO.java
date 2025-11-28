package com.example.solveit.api;

// Esta é a classe DTO no seu projeto Android. Ela precisa do construtor.
public class InteracaoDTO {
    private int id_interacoes;
    private int id_chamado;
    private int id_usuario;
    private String nome_usuario;
    private String mensagem;
    private String dt_interacao;

    // ✨ CORREÇÃO 1: Adicionando o construtor de 6 argumentos ✨
    public InteracaoDTO(int id_interacoes, int id_chamado, int id_usuario, String nome_usuario, String mensagem, String dt_interacao) {
        this.id_interacoes = id_interacoes;
        this.id_chamado = id_chamado;
        this.id_usuario = id_usuario;
        this.nome_usuario = nome_usuario;
        this.mensagem = mensagem;
        this.dt_interacao = dt_interacao;
    }

    // ✨ CORREÇÃO 2: Adicionando o construtor vazio (padrão) para o GSON, se necessário ✨
    public InteracaoDTO() { }


    // Getters para o Gson funcionar e para a TimelineAdapter
    public int getId_interacoes() { return id_interacoes; }
    public int getId_chamado() { return id_chamado; }
    public int getId_usuario() { return id_usuario; }
    public String getNome_usuario() { return nome_usuario; }
    public String getMensagem() { return mensagem; }
    public String getDt_interacao() { return dt_interacao; }
}