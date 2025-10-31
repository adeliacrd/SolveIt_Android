package com.example.solveit.api;

// Este é o "molde" para a lista RESUMIDA de chamados
public class ChamadoDTO {
    // Os nomes DEVEM ser idênticos aos do JSON
    private int id_chamado;
    private String titulo;
    private String desc_prioridade;
    private String desc_status;

    // Getters (essenciais para o Gson)
    public int getId_chamado() { return id_chamado; }
    public String getTitulo() { return titulo; }
    public String getDesc_prioridade() { return desc_prioridade; }
    public String getDesc_status() { return desc_status; }
}
