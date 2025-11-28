package com.example.solveit_backend;

// DTO (Data Transfer Object) para enviar os dados de um chamado para o app
public class ChamadoDTO {
    // Atenção aos nomes, o app (Gson) vai usá-los no JSON
    private int id_chamado;
    private String titulo;
    private String desc_prioridade;
    private String desc_status;
    private int id_usuario;
    private Integer id_usuario_atribuido;

    private String dt_atualizacao;
    private String nota_avaliacao;
    private String dt_avaliacao;

    // ✨ NOVOS CAMPOS PARA A EXPANSÃO ✨
    private String desc_chamado;     // Descrição completa
    private String nome_solicitante; // Nome de quem abriu

    // ✨ CAMPO DE CONTROLE DE UI (Não vem do banco) ✨
    private boolean isExpanded = false; // Começa fechado

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
    public int getId_usuario() {return id_usuario;}
    public Integer getId_usuario_atribuido() {return id_usuario_atribuido;}
    public String getDt_atualizacao() {return dt_atualizacao;}
    public String getNota_avaliacao() {return nota_avaliacao;}
    public String getDt_avaliacao() {return dt_avaliacao;}

    // ✨ NOVOS GETTERS ✨
    public String getDesc_chamado() { return desc_chamado; }
    public String getNome_solicitante() { return nome_solicitante; }

    // ✨ GETTER E SETTER PARA O ESTADO EXPANDIDO ✨
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}
