package com.example.solveit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager; // ✨ NOVO IMPORT
import androidx.recyclerview.widget.RecyclerView;     // ✨ NOVO IMPORT

import com.example.solveit.api.ApiService;
import com.example.solveit.api.ChamadoDTO;           // ✨ NOVO IMPORT
import com.example.solveit.api.RetrofitClient;

import java.util.ArrayList;                          // ✨ NOVO IMPORT
import java.util.List;                               // ✨ NOVO IMPORT

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdmHomeActivity extends AppCompatActivity {

    private static final String TAG = "AdmHomeActivity";
    private int idUsuarioLogado;
    private int idTipoAcessoLogado;

    // Variáveis para os ícones da barra
    private ImageButton iconConfig, iconAdd, iconNotificacoes, iconPerfil;

    // ✨ NOVAS VARIÁVEIS PARA O RECYCLERVIEW E ADAPTER ✨
    private RecyclerView recyclerViewChamados;
    private ChamadosAdapter chamadosAdapter;
    private List<ChamadoDTO> listaDeChamados; // Lista que será exibida

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm_home);

        // --- 1. Lê os dados do usuário (ID e Nível de Acesso) ---
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        idUsuarioLogado = prefs.getInt(MainActivity.KEY_USER_ID, -1);
        idTipoAcessoLogado = prefs.getInt(MainActivity.KEY_USER_ROLE_ID, -1);

        Log.d(TAG, "Tela ADM iniciada por: ID " + idUsuarioLogado + ", Nível " + idTipoAcessoLogado);

        // --- 2. Configura a Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar_icones);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // --- 3. Conecta os Ícones do XML com o Java e configura a visibilidade ---
        iconConfig = findViewById(R.id.icon_config);
        iconAdd = findViewById(R.id.icon_add);
        iconNotificacoes = findViewById(R.id.ic_notifications);
        iconPerfil = findViewById(R.id.btn_profile);

        if (idTipoAcessoLogado == 3) { // Se for ADM
            iconConfig.setVisibility(View.VISIBLE);
        } else {
            iconConfig.setVisibility(View.GONE);
        }
        // (Lógica para outros botões, como iconAdd, se necessário)

        // --- 4. Configura os Cliques dos Ícones ---
        iconConfig.setOnClickListener(v -> Toast.makeText(this, "Configurações (ADM) clicado!", Toast.LENGTH_SHORT).show());
        iconAdd.setOnClickListener(v -> startActivity(new Intent(this, AberturaChamadoActivity.class)));
        iconNotificacoes.setOnClickListener(v -> Toast.makeText(this, "Notificações clicado!", Toast.LENGTH_SHORT).show());
        iconPerfil.setOnClickListener(v -> Toast.makeText(this, "Perfil clicado!", Toast.LENGTH_SHORT).show());

        // --- 5. Configura o RecyclerView ---
        recyclerViewChamados = findViewById(R.id.recyclerView_adm_chamados);
        recyclerViewChamados.setLayoutManager(new LinearLayoutManager(this));

        listaDeChamados = new ArrayList<>(); // Inicializa a lista vazia
        chamadosAdapter = new ChamadosAdapter(this, listaDeChamados);
        recyclerViewChamados.setAdapter(chamadosAdapter);

        // ✨ Opcional: Configura o clique nos itens da lista para abrir os detalhes
        chamadosAdapter.setOnItemClickListener(idChamado -> {
            Toast.makeText(AdmHomeActivity.this, "Chamado #" + idChamado + " clicado!", Toast.LENGTH_SHORT).show();
            // (Aqui você abriria a tela de Detalhes do Chamado, passando o idChamado)
            // Intent intent = new Intent(AdmHomeActivity.this, DetalheChamadoActivity.class);
            // intent.putExtra("ID_CHAMADO", idChamado);
            // startActivity(intent);
        });

        // --- 6. Busca os chamados da API ---
        buscarChamadosDaApi();
    }

    /**
     * Método para buscar a lista de chamados da API.
     */
    private void buscarChamadosDaApi() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<ChamadoDTO>> call = apiService.getChamados(); // Chama o endpoint que retorna a lista de ChamadoDTO

        call.enqueue(new Callback<List<ChamadoDTO>>() {
            @Override
            public void onResponse(Call<List<ChamadoDTO>> call, Response<List<ChamadoDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaDeChamados.clear();
                    listaDeChamados.addAll(response.body());
                    chamadosAdapter.notifyDataSetChanged(); // Atualiza o RecyclerView
                    Log.d(TAG, "Chamados carregados: " + listaDeChamados.size());

                    // ✨ Pinta as prioridades ao carregar os dados ✨
                    // Isso já é feito no onBindViewHolder do adapter, não precisa de loop aqui
                } else {
                    String errorMsg = "Falha ao buscar chamados. Código: " + response.code();
                    try {
                        errorMsg += " - " + response.errorBody().string();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, errorMsg);
                    Toast.makeText(AdmHomeActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChamadoDTO>> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao buscar chamados: " + t.getMessage(), t);
                Toast.makeText(AdmHomeActivity.this, "Erro de conexão ao buscar chamados.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarrega a lista de chamados toda vez que a tela volta a ser visível
        // Isso é útil se um novo chamado foi aberto ou um status foi atualizado em outra tela.
        buscarChamadosDaApi();
    }
}