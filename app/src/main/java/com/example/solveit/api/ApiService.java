package com.example.solveit.api; // O seu pacote

// ✨ IMPORTS CORRIGIDOS E LIMPOS ✨
// Importa todos os "moldes" (DTOs) que seu app vai usar
import com.example.solveit.api.LoginResponse;
import com.example.solveit.api.RegisterResponse;
import com.example.solveit.api.AbrirChamadoResponse;
import com.example.solveit.api.CategoriaDTO;
import com.example.solveit.api.ChamadoDTO;           // O "molde" da lista Mestre
import com.example.solveit.api.ChamadoCompletoDTO; // O "molde" da tela de Detalhe

// Imports do Java e Retrofit
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query; // ✨ ESTE É O IMPORT CORRETO PARA @Query ✨


/**
 * Interface (Cardápio) do Retrofit que define todos os endpoints da API.
 * Esta é a versão final e limpa, combinando todas as funcionalidades.
 */
public interface ApiService {

    // --- AUTENTICAÇÃO ---

    // Define o método POST para a rota "api/login"
    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> loginUsuario(
            @Field("email") String email,
            @Field("senha") String senha
    );

    // Define o método POST para a rota "api/register" (A sua versão nova e correta)
    @FormUrlEncoded
    @POST("api/register")
    Call<RegisterResponse> registerUsuario(
            @Field("fullName") String fullName,
            @Field("cpf") String cpf,
            @Field("email") String email,
            @Field("companyName") String companyName,
            @Field("loginSugerido") String loginSugerido,
            @Field("password") String password
    );

    // --- ABERTURA DE CHAMADO ---

    // 1. Método para buscar a lista de categorias (para o Spinner)
    @GET("api/categorias")
    Call<List<CategoriaDTO>> getCategorias();

    // 2. Método para abrir/criar um novo chamado (POST)
    @FormUrlEncoded
    @POST("api/chamados")
    Call<AbrirChamadoResponse> abrirChamado(
            @Field("titulo") String titulo,
            @Field("id_usuario_abertura") int idUsuarioAbertura,
            @Field("prioridade") String prioridade, // O texto: "Média", "Alta", etc.
            @Field("id_categoria") int idCategoria, // O ID numérico selecionado
            @Field("email") String email,
            @Field("descricao") String descricao
    );

    // --- VISUALIZAÇÃO DE CHAMADOS (Mestre-Detalhe) ---

    // 1. MESTRE: Busca a lista SIMPLES de todos os chamados
    // (Este substitui os dois métodos duplicados que você tinha)
    @GET("api/chamados")
    Call<List<ChamadoDTO>> getChamados();

    // 2. DETALHE: Busca um chamado COMPLETO pelo seu ID
    @GET("api/chamados")
    Call<ChamadoCompletoDTO> getDetalhesChamado(@Query("id_chamado") int idChamado);
}