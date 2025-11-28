package com.example.solveit_backend;

import java.util.List;

// DTO para a tela de "Informações" (detalhe completo)
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
    private String nome_solicitante; // Nome do usuário que abriu
    private String nome_agente; // Nome do agente/adm atribuído (pode ser nulo)

    private List<InteracaoDTO> timeline;

    // Getters para a timeline
    public List<InteracaoDTO> getTimeline() { return timeline; }

    // Construtor (Getters são necessários para o Gson)
    // (Um construtor completo é grande, podemos omitir para focar nos Getters,
    // mas vamos adicionar um para preencher no Servlet)
    public ChamadoCompletoDTO(int id_chamado, String titulo, String desc_chamado,
                              String dt_abertura, String dt_fechamento, String email_contato,
                              String desc_prioridade, String desc_status, String desc_categoria,
                              String nome_solicitante, String nome_agente) {
        this.id_chamado = id_chamado;
        this.titulo = titulo;
        this.desc_chamado = desc_chamado;
        this.dt_abertura = dt_abertura;
        this.dt_fechamento = dt_fechamento;
        this.email_contato = email_contato;
        this.desc_prioridade = desc_prioridade;
        this.desc_status = desc_status;
        this.desc_categoria = desc_categoria;
        this.nome_solicitante = nome_solicitante;
        this.nome_agente = nome_agente;
    }

    // Getters
    public int getId_chamado() { return id_chamado; }
    public String getTitulo() { return titulo; }
    public String getDesc_chamado() { return desc_chamado; }
    public String getDt_abertura() { return dt_abertura; }
    // ... (Getters para todos os outros campos)
}
