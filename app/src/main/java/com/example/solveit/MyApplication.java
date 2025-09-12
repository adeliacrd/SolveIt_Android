package com.example.solveit; // Certifique-se que o pacote está correto

import android.app.Application;         // Importe a classe Application
import com.google.firebase.FirebaseApp; // Importe para o Firebase

public class MyApplication extends Application { // Faça sua classe ESTENDER Application

    @Override
    public void onCreate() {
        super.onCreate(); // É crucial chamar o método da superclasse primeiro

        // Inicialize o Firebase aqui
        FirebaseApp.initializeApp(this);

        // Você pode adicionar outras inicializações globais para seu app aqui no futuro
        // Exemplo: Log.d("MyApplication", "Aplicativo inicializado!");
    }
}