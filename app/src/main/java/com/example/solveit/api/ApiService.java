package com.example.solveit.api; // SEU PACOTE

// ✨ IMPORTS CORRIGIDOS (Juntando os seus e os dela) ✨
import com.example.solveit.Chamado; // Importa a classe 'Chamado' que ela criou
import com.example.solveit.api.LoginResponse;
import com.example.solveit.api.RegisterResponse;
import java.util.List; // Importa o List
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET; // Importa o GET
import retrofit2.http.POST;

public interface ApiService {

    // --- CÓDIGO DA SUA AMIGA (ESSENCIAL) ---
    // Você precisa ADICIONAR este método
    // ✨ ATENÇÃO: Corrigi a rota para "api/chamados" (a dela estava "chamados") ✨
    @GET("api/chamados")
    Call<List<Chamado>> getChamados();

    // --- SEU CÓDIGO (Login) ---
    // Este já estava nos dois, está correto
    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> loginUsuario(
            @Field("email") String email,
            @Field("senha") String senha
    );

    // --- SEU CÓDIGO (Registro NOVO) ---
    // Você deve manter a SUA versão, que é a mais nova (com CPF)
    @FormUrlEncoded
    @POST("api/register")
    Call<RegisterResponse> registerUsuario(
            @Field("fullName") String fullName,
            @Field("cpf") String cpf,
            @Field("email") String email,
            @Field("companyName") String companyName,
            @Field("loginSugerido") String loginSugerido,
            @Field("password") String password
            // O campo companySize foi removido
    );
}