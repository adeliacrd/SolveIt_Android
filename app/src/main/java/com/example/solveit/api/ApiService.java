package com.example.solveit.api; // USE SEU PACOTE CORRETO

import com.example.solveit.api.LoginResponse; // Ajuste se o pacote for diferente
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    // Define o método POST para a rota "api/login"
    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> loginUsuario(
            @Field("email") String email,
            @Field("senha") String senha
    );

    @FormUrlEncoded
    @POST("api/register")
    Call<RegisterResponse> registerUsuario(
            @Field("fullName") String fullName,
            @Field("email") String email,
            @Field("password") String password,
            @Field("companyName") String companyName,
            @Field("companySize") String companySize // Novo campo
    );
}