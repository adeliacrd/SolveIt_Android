package com.example.solveit.api; // O seu pacote

// ✨ IMPORTS CORRIGIDOS E LIMPOS ✨
// Importa todos os "moldes" (DTOs) que seu app vai usar
import com.example.solveit.Notificacao;
import com.example.solveit.api.LoginResponse;
import com.example.solveit.api.RegisterResponse;
import com.example.solveit.api.AbrirChamadoResponse;
import com.example.solveit.api.CategoriaDTO;
import com.example.solveit.api.ChamadoDTO;           // O "molde" da lista Mestre
import com.example.solveit.api.ChamadoCompletoDTO; // O "molde" da tela de Detalhe
import com.example.solveit.api.AtribuicaoResponse;

// Imports do Java e Retrofit
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query; // ✨ ESTE É O IMPORT CORRETO PARA @Query ✨
// ✨ IMPORTS NOVOS NECESSÁRIOS PARA UPLOAD ✨
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart; // Import para @Multipart
import retrofit2.http.POST;
import retrofit2.http.Part;      // Import para @Part
import retrofit2.http.Query;


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


    @GET("api/notificacoes") // ou o caminho exato da sua API de notificações
    Call<List<Notificacao>> getNotificacoes();

    @FormUrlEncoded
    @POST("api/atribuir") // O endereço que vai bater no backend
    Call<AtribuicaoResponse> assumirChamado(
            @Field("id_chamado") int idChamado,
            @Field("id_usuario") int idUsuario
    );

    // ✨ ATUALIZAR STATUS (CONCLUIR/CANCELAR) ✨
    @FormUrlEncoded
    @POST("api/status")
    Call<AtribuicaoResponse> atualizarStatus(
            @Field("id_chamado") int idChamado,
            @Field("novo_status") int novoStatus // 4 ou 5
    );

    // 1. Enviar Mensagem de Texto
    @FormUrlEncoded
    @POST("api/comentarios")
    Call<AtribuicaoResponse> enviarComentario(
            @Field("id_chamado") int idChamado,
            @Field("id_usuario") int idUsuario,
            @Field("mensagem") String mensagem
    );

    // 2. Enviar Arquivo (Multipart)
    @Multipart
    @POST("api/upload")
    Call<AtribuicaoResponse> uploadArquivo(
            @Part("id_chamado") okhttp3.RequestBody idChamado,
            @Part okhttp3.MultipartBody.Part arquivo
    );
}