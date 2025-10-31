package com.example.solveit.api;

// Este é o "molde" para a visão COMPLETA de um chamado
public class ChamadoCompletoDTO {
    // Info do Chamado
    private int id_chamado;
    private String titulo;
    private String desc_chamado;
    private String dt_abertura;
    private String dt_fechamento;
    private String email_contato;

    // Info das Tabelas JOIN
    private String desc_prioridade;
    private String desc_status;
    private String desc_categoria;
    private String nome_solicitante;
    private String nome_agente;

    // Getters para todos os campos (essenciais para o Gson)
    public int getId_chamado() { return id_chamado; }
    public String getTitulo() { return titulo; }
    public String getDesc_chamado() { return desc_chamado; }
    public String getDt_abertura() { return dt_abertura; }
    public String getDt_fechamento() { return dt_fechamento; }
    public String getEmail_contato() { return email_contato; }
    public String getDesc_prioridade() { return desc_prioridade; }
    public String getDesc_status() { return desc_status; }
    public String getDesc_categoria() { return desc_categoria; }
    public String getNome_solicitante() { return nome_solicitante; }
    public String getNome_agente() { return nome_agente; }
}