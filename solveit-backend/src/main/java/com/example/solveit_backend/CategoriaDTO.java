package com.example.solveit_backend;

// Classe simples para transferir dados da Categoria para o frontend
public class CategoriaDTO {
    private int id_categoria;
    private String desc_categoria;

    // Construtor
    public CategoriaDTO(int id, String descricao) {
        this.id_categoria = id;
        this.desc_categoria = descricao;
    }

    // Getters são necessários para o Gson converter para JSON
    public int getId_categoria() {
        return id_categoria;
    }

    public String getDesc_categoria() {
        return desc_categoria;
    }
}