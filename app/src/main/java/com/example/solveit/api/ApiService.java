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
import retrofit2.http.GET; //Adicionado pós abertura de chamado
import java.util.List;

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

    // 1. Método para buscar a lista de categorias
    @GET("api/categorias") // Usa GET para buscar dados
    Call<List<CategoriaDTO>> getCategorias(); // Retorna uma lista de objetos CategoriaDTO

    // 2. Método para abrir/criar um novo chamado
    @FormUrlEncoded // Envia dados como formulário
    @POST("api/chamados") // Usa POST para criar um novo recurso
    Call<AbrirChamadoResponse> abrirChamado(
            @Field("titulo") String titulo,
            @Field("id_usuario_abertura") int idUsuarioAbertura,
            @Field("prioridade") String prioridade, // O texto: "Média", "Alta", etc.
            @Field("id_categoria") int idCategoria, // O ID numérico selecionado
            @Field("email") String email,
            @Field("descricao") String descricao
            // @Field("id_usuario_logado") int idUsuarioLogado // Adicione se precisar enviar quem abriu
    );
}