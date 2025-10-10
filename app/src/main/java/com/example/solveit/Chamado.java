package com.example.solveit;

// Esta classe é o "molde" para os dados de um chamado.
public class Chamado {
    private int id;
    private String titulo;
    private String prioridade;
    private String status;

    // Construtor: usado para criar um novo objeto 'Chamado'
    public Chamado(int id, String titulo, String prioridade, String status) {
        this.id = id;
        this.titulo = titulo;
        this.prioridade = prioridade;
        this.status = status;
    }

    // Getters: métodos para o Adapter poder ler os dados de cada chamado.
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getPrioridade() { return prioridade; }
    public String getStatus() { return status; }
}
