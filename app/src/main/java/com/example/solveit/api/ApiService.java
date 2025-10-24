package com.example.solveit.api;

import com.example.solveit.Chamado; // Importa o modelo de Chamado
import com.example.solveit.api.LoginResponse;
import com.example.solveit.api.RegisterResponse;

import java.util.List; // Importa a List

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET; // IMPORTANTE: Importa o GET
import retrofit2.http.POST;

public interface ApiService {

    // --- Endpoint para buscar a lista de chamados ---
    // ✨ ESTE É O MÉTODO QUE ESTAVA FALTANDO ✨
    // Ele faz uma requisição GET para a rota "chamados" (ou a que for na sua API)
    @GET("chamados")
    Call<List<Chamado>> getChamados();

    // Endpoint de Login (seu código original)
    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> loginUsuario(
            @Field("email") String email,
            @Field("senha") String senha
    );

    // Endpoint de Registro (seu código original)
    @FormUrlEncoded
    @POST("api/register")
    Call<RegisterResponse> registerUsuario(
            @Field("fullName") String fullName,
            @Field("email") String email,
            @Field("password") String password,
            @Field("companyName") String companyName,
            @Field("companySize") String companySize
    );
}
