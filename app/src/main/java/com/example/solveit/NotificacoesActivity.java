package com.example.solveit;

import android.os.Bundle;
import android.util.Log; // ✨ Adicionado para logs de depuração da API
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// ✨ Imports necessários para a chamada de API com Retrofit ✨
import com.example.solveit.api.ApiService;
import com.example.solveit.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

// ✨ Imports do Retrofit ✨
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Usando o nome de classe NotificacoesActivity
public class NotificacoesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textViewNenhumaNotificacao;
    private NotificacaoAdapter adapter;
    private List<Notificacao> listaNotificacoes;

    private static final String TAG = "NotificacoesActivity"; // ✨ Adicionado para logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        // 1. Configuração da Barra Superior e Botão Voltar (permanece igual)
        ImageButton btnVoltarTopo = findViewById(R.id.btn_voltar_notificacoes);
        btnVoltarTopo.setOnClickListener(v -> finish());

        // 2. Inicialização dos componentes da lista (permanece igual)
        recyclerView = findViewById(R.id.recycler_view_notificacoes);
        textViewNenhumaNotificacao = findViewById(R.id.text_view_nenhuma_notificacao);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Inicializa a lista como VAZIA.
        listaNotificacoes = new ArrayList<>();

        // 4. Inicializa o Adapter e atribui ao RecyclerView.
        adapter = new NotificacaoAdapter(listaNotificacoes);
        recyclerView.setAdapter(adapter);

        // 5. ✨ A MUDANÇA PRINCIPAL: Chama o método para buscar dados da API ✨
        buscarNotificacoesDaApi();

        // Configuração dos outros botões da Toolbar (permanece igual)
        ImageButton actionAdd = findViewById(R.id.action_add);
        actionAdd.setOnClickListener(v -> Toast.makeText(this, "Ação Adicionar Clicada", Toast.LENGTH_SHORT).show());

        ImageButton actionProfile = findViewById(R.id.action_profile);
        actionProfile.setOnClickListener(v -> Toast.makeText(this, "Ação Perfil Clicada", Toast.LENGTH_SHORT).show());
    }

    /**
     * ✨ NOVO MÉTODO: Faz a chamada à API para buscar as notificações reais.
     */
    private void buscarNotificacoesDaApi() {
        Log.d(TAG, "Iniciando busca de notificações na API...");

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Notificacao>> call = apiService.getNotificacoes();

        call.enqueue(new Callback<List<Notificacao>>() {
            @Override
            public void onResponse(Call<List<Notificacao>> call, Response<List<Notificacao>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Limpa a lista antiga e adiciona os novos dados da API
                    listaNotificacoes.clear();
                    listaNotificacoes.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Atualiza a tela com os novos dados
                } else {
                    Log.e(TAG, "Falha ao carregar notificações. Código: " + response.code());
                    Toast.makeText(NotificacoesActivity.this, "Falha ao carregar notificações.", Toast.LENGTH_SHORT).show();
                }
                // Após a resposta (sucesso ou falha), verifica se a lista está vazia
                verificarVisibilidadeDaLista();
            }

            @Override
            public void onFailure(Call<List<Notificacao>> call, Throwable t) {
                Log.e(TAG, "Erro de conexão ao buscar notificações.", t);
                Toast.makeText(NotificacoesActivity.this, "Erro de conexão. Verifique a internet.", Toast.LENGTH_SHORT).show();
                // Em caso de falha de conexão, a lista estará vazia, então atualizamos a UI
                verificarVisibilidadeDaLista();
            }
        });
    }

    /**
     * ✨ NOVO MÉTODO: Centraliza a lógica para mostrar ou esconder a mensagem de lista vazia.
     */
    private void verificarVisibilidadeDaLista() {
        if (listaNotificacoes.isEmpty()) {
            textViewNenhumaNotificacao.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewNenhumaNotificacao.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * ✨ MÉTODO ANTIGO REMOVIDO: Este método não é mais necessário, pois os dados vêm da API.
     *
     * private void popularListaNotificacoes() {
     *     listaNotificacoes.add(new Notificacao(...));
     *     ...
     * }
     */
}