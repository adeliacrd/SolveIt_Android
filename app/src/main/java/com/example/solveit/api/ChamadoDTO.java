package com.example.solveit.api;

// Este é o "molde" para a lista RESUMIDA de chamados (Mestre)
public class ChamadoDTO {
    // Campos que vêm do Backend (os nomes precisam ser idênticos ao JSON)
    private int id_chamado;
    private String titulo;
    private String desc_prioridade;
    private String desc_status;
    private int id_usuario;
    private Integer id_usuario_atribuido;
    private String dt_atualizacao;
    private String nota_avaliacao;
    private String dt_avaliacao;
    private String desc_chamado; // Para a expansão (descrição)
    private String nome_solicitante; // ✨ CAMPO FALTANTE ✨

    // Campo de controle de UI (Não vem do banco, é interno do Adapter)
    private boolean isExpanded = false;

    // ==================
    // GETTERS
    // ==================
    public int getId_chamado() { return id_chamado; }
    public String getTitulo() { return titulo; }
    public String getDesc_prioridade() { return desc_prioridade; }
    public String getDesc_status() { return desc_status; }
    public int getId_usuario() { return id_usuario; }
    public Integer getId_usuario_atribuido() { return id_usuario_atribuido; }
    public String getDt_atualizacao() { return dt_atualizacao; }
    public String getNota_avaliacao() { return nota_avaliacao; }
    public String getDt_avaliacao() { return dt_avaliacao; }
    public String getDesc_chamado() { return desc_chamado; }

    // ✨ O GETTER QUE ESTAVA FALTANDO PARA RESOLVER O ERRO ✨
    public String getNome_solicitante() { return nome_solicitante; }

    // Getters e Setters para o Estado Expandido (para o Adapter)
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}
