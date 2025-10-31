package com.example.solveit_backend;

// DTO (Data Transfer Object) para enviar os dados de um chamado para o app
public class ChamadoDTO {
    // Atenção aos nomes, o app (Gson) vai usá-los no JSON
    private int id_chamado;
    private String titulo;
    private String desc_prioridade;
    private String desc_status;
    // Adicione mais campos se a sua tela de ADM precisar (ex: nome do usuário)
    // private String nome_usuario;

    // Construtor
    public ChamadoDTO(int id_chamado, String titulo, String desc_prioridade, String desc_status) {
        this.id_chamado = id_chamado;
        this.titulo = titulo;
        this.desc_prioridade = desc_prioridade;
        this.desc_status = desc_status;
    }

    // Getters são necessários para o Gson
    public int getId_chamado() { return id_chamado; }
    public String getTitulo() { return titulo; }
    public String getDesc_prioridade() { return desc_prioridade; }
    public String getDesc_status() { return desc_status; }
}
