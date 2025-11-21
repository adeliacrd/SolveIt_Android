package com.example.solveit;

// Esta classe é o "molde" ou a "planta" para os dados de uma única mensagem.
// Ela não é uma tela, apenas uma forma de organizar os dados.
public class Mensagem {
    // 1. Atributos (as informações que uma mensagem contém)
    private String iniciais;    // "NC" ou "NA"
    private String nomeUsuario; // "NomeCliente"
    private String tipo;        // "Descrição" ou "Mensagem"
    private String data;        // "29/04/2025 às 16h32"
    private String texto;       // "Descrição aqui..."

    // 2. Construtor: um atalho para criar um novo objeto Mensagem já com todos os dados.
    public Mensagem(String iniciais, String nomeUsuario, String tipo, String data, String texto) {
        this.iniciais = iniciais;
        this.nomeUsuario = nomeUsuario;
        this.tipo = tipo;
        this.data = data;
        this.texto = texto;
    }

    // 3. Getters: Métodos que permitem que outras classes (como o MensagensAdapter) leiam os dados.
    public String getIniciais() {
        return iniciais;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public String getData() {
        return data;
    }

    public String getTexto() {
        return texto;
    }
}
