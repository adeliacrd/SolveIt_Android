package com.example.solveit.api;

// ✨ PASSO 1: ADICIONE ESTE IMPORT ✨
import java.io.Serializable;

// Este é o "molde" para a lista RESUMIDA de chamados
// ✨ PASSO 2: ADICIONE "implements Serializable" AQUI ✨
public class ChamadoDTO implements Serializable {
    // Os nomes DEVEM ser idênticos aos do JSON
    private int id_chamado;
    private String titulo;
    private String desc_prioridade;
    private String desc_status;
    private int id_usuario;
    // Adicione um campo para a descrição se ele vier da API
    private String descricao;

    // Getters (essenciais para o Gson e para o seu código)
    public int getId_chamado() { return id_chamado; }
    public String getTitulo() { return titulo; }

    // Este getter é necessário para a tela de detalhes
    public String getDescricao() { return descricao; }

    public String getDesc_prioridade() { return desc_prioridade; }
    public String getDesc_status() { return desc_status; }
    public int getId_usuario() { return id_usuario; }
}
