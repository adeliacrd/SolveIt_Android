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
    private int id_usuario;

    // ✨ NOVOS CAMPOS ✨
    private String email_solicitante; // E-mail do cadastro do solicitante
    private String email_agente;      // E-mail do cadastro do agente
    private int sla_horas;            // Horas do SLA

    private List<InteracaoDTO> timeline;

    // Getters para a timeline
    public List<InteracaoDTO> getTimeline() { return timeline; }

    // Construtor (Getters são necessários para o Gson)
    // (Um construtor completo é grande, podemos omitir para focar nos Getters,
    // mas vamos adicionar um para preencher no Servlet)
    public ChamadoCompletoDTO(int id_chamado, String titulo, String desc_chamado,
                              String dt_abertura, String dt_fechamento, String email_contato,
                              String desc_prioridade, String desc_status, String desc_categoria,
                              String nome_solicitante, String nome_agente, Integer sla_horas) {
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
        this.sla_horas = sla_horas;
    }

    // Os Getters (O Retrofit/Gson usa isso para ler os dados)
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
    public int getId_usuario() { return id_usuario; }

    // ✨ NOVOS GETTERS ✨
    public String getEmail_solicitante() { return email_solicitante; }
    public String getEmail_agente() { return email_agente; }
    public int getSla_horas() { return sla_horas; }
}
