package com.example.solveit.api; // USE SEU PACOTE CORRETO

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Verifique se sua BASE_URL está correta
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 1. Crie o interceptor de logging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            // Defina o nível de log para BODY para ver tudo: URL, cabeçalhos e corpo da requisição/resposta
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Crie um cliente OkHttp e adicione o interceptor a ele
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // 3. Construa a instância do Retrofit, passando o cliente personalizado
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // <-- PONTO CHAVE: Usar o cliente com o logger
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}