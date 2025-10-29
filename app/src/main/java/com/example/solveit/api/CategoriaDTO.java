package com.example.solveit.api; // Use seu pacote

// Importe se precisar de anotações do Gson (opcional aqui)
// import com.google.gson.annotations.SerializedName;

public class CategoriaDTO {
    // Os nomes das variáveis DEVEM bater com o JSON retornado pela API
    private int id_categoria;
    private String desc_categoria;

    // Construtor vazio (bom para Gson)
    public CategoriaDTO() {}

    // Getters (ESSENCIAIS para o Gson/Retrofit funcionar)
    public int getId_categoria() {
        return id_categoria;
    }

    public String getDesc_categoria() {
        return desc_categoria;
    }

    // ✨ IMPORTANTE PARA O SPINNER: ✨
    // Sobrescrevemos o método toString() para que o ArrayAdapter mostre
    // a descrição da categoria diretamente no Spinner.
    @Override
    public String toString() {
        return desc_categoria != null ? desc_categoria : ""; // Retorna a descrição ou vazio se for nulo
    }
}