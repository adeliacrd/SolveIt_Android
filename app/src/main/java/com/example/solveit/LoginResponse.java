// Cole este código inteiro no seu novo arquivo LoginResponse.java

package com.example.solveit;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    // Esta anotação diz ao Retrofit para procurar um campo chamado "success" no JSON
    // e colocar seu valor nesta variável 'success'.
    @SerializedName("success")
    private boolean success;

    // Procura por "message" no JSON.
    @SerializedName("message")
    private String message;

    // Procura por "admin" no JSON.
    // Este é o campo que nos dirá se o usuário é um administrador.
    @SerializedName("admin")
    private boolean admin;

    // --- Métodos "Getters" ---
    // São métodos públicos que usamos para LER os valores das variáveis privadas acima.

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Este é o método que usaremos na MainActivity para saber se é um admin.
    public boolean isAdmin() {
        return admin;
    }
}
