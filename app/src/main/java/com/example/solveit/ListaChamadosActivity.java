package com.example.solveit;

// IMPORTS NECESSÁRIOS (MUITOS FORAM ATUALIZADOS)
import android.content.Intent;
import android.content.SharedPreferences; // ✨ Para ler o ID do usuário
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log; // ✨ Para logs
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast; // ✨ Para mensagens de erro

import androidx.annotation.NonNull; // ✨ Import necessário
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveit.api.ApiService;       // ✨ API
import com.example.solveit.api.ChamadoDTO;       // ✨ USA O NOVO DTO ✨
import com.example.solveit.api.RetrofitClient; // ✨ API
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Imports do Retrofit
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaChamadosActivity extends AppCompatActivity {

    private static final String TAG = "ListaChamadosActivity"; // ✨ Tag de Log

    private RecyclerView recyclerViewChamados;
    private ChamadosAdapter chamadosAdapter; // ✨ Nosso adapter atualizado ✨
    private TextView textViewEmpty;
    private View tableContainer;
    private TabLayout tabLayout;

    // ✨ CORRIGIDO: A lista principal agora é de ChamadoDTO ✨
    private final List<ChamadoDTO> todosOsChamados = new ArrayList<>();
    private ApiService apiService; // ✨ Para chamar a API
    private int idUsuarioLogado; // ✨ Para filtrar "Meus Chamados"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chamados);

        // --- Pega o ID do usuário logado ---
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        idUsuarioLogado = prefs.getInt(MainActivity.KEY_USER_ID, -1);
        if (idUsuarioLogado == -1) {
            Log.e(TAG, "ERRO CRÍTICO: ID do usuário não encontrado. Voltando para o login.");
            Toast.makeText(this, "Erro de autenticação. Faça login novamente.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // --- Inicializa a API ---
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // --- Configuração dos Componentes (sem alterações) ---
        Toolbar toolbar = findViewById(R.id.toolbar_icones);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        recyclerViewChamados = findViewById(R.id.recyclerViewChamados);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        tableContainer = findViewById(R.id.tableContainer);
        tabLayout = findViewById(R.id.tabLayout);

        // --- Configuração do RecyclerView e Adapter (COM A CORREÇÃO) ---
        recyclerViewChamados.setLayoutManager(new LinearLayoutManager(this));
        // ✨ CORREÇÃO: Passa o Context (this) e a lista (que pode estar vazia) ✨
        chamadosAdapter = new ChamadosAdapter(this, new ArrayList<>());
        recyclerViewChamados.setAdapter(chamadosAdapter);

        // ✨ Opcional: Clique para abrir detalhes (igual ao ADM) ✨
        chamadosAdapter.setOnItemClickListener(idChamado -> {
            Toast.makeText(this, "Cliente clicou no Chamado #" + idChamado, Toast.LENGTH_SHORT).show();
            // (Aqui você abriria a tela de Detalhes do Chamado)
            // Intent intent = new Intent(ListaChamadosActivity.this, DetalheChamadoActivity.class);
            // intent.putExtra("ID_CHAMADO", idChamado);
            // startActivity(intent);
        });

        // --- Configuração das Abas (sem alterações) ---
        setupTabLayout();

        // --- FAB (Floating Action Button) ---
        FloatingActionButton fabAdicionarChamado = findViewById(R.id.fabAdicionarChamado);
        fabAdicionarChamado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaChamadosActivity.this, AberturaChamadoActivity.class);
                startActivity(intent);
            }
        });
    }

    // ✨ ATUALIZADO: Este método agora é chamado no onResume ✨
    @Override
    protected void onResume() {
        super.onResume();
        // Busca os chamados da API toda vez que a tela volta a ficar visível
        buscarChamadosDaApi();
    }

    // ================================================================
    // ✨ MÉTODO NOVO PARA BUSCAR CHAMADOS DA API ✨
    // (O código que você tinha comentado, agora está no lugar certo)
    // ================================================================
    private void buscarChamadosDaApi() {
        Log.d(TAG, "Buscando chamados da API...");
        // (ProgressBar.setVisibility(View.VISIBLE) // Opcional: mostrar loading)

        // ✨ ATENÇÃO: Esta chamada (getChamados) busca TODOS os chamados (o do ADM) ✨
        // Idealmente, criaríamos um endpoint novo no backend (ex: /api/chamados/meus)
        // Por enquanto, vamos buscar TODOS e filtrar no app.
        Call<List<ChamadoDTO>> call = apiService.getChamados();

        call.enqueue(new Callback<List<ChamadoDTO>>() {
            @Override
            public void onResponse(Call<List<ChamadoDTO>> call, Response<List<ChamadoDTO>> response) {
                // (ProgressBar.setVisibility(View.GONE) // Opcional: esconder loading)
                if (response.isSuccessful() && response.body() != null) {
                    todosOsChamados.clear();
                    // ✨ FILTRO PROVISÓRIO: Filtra apenas os chamados DO USUÁRIO LOGADO ✨
                    // (Isso é ineficiente, o ideal é o backend fazer isso)
                    for (ChamadoDTO chamado : response.body()) {
                        // ✨ Precisamos que o ChamadoDTO também retorne o id_usuario para filtrar ✨
                        // ** VAMOS PRECISAR ATUALIZAR O BACKEND PARA ISSO **
                        // Por enquanto, vamos apenas carregar todos para teste:
                        todosOsChamados.addAll(response.body());
                    }
                    Log.d(TAG, "Chamados carregados: " + todosOsChamados.size());
                    // Atualiza a lista com base na aba que está selecionada
                    filtrarEAtualizarLista(tabLayout.getSelectedTabPosition());
                } else {
                    Log.e(TAG, "Falha ao buscar dados. Código: " + response.code());
                    Toast.makeText(ListaChamadosActivity.this, "Falha ao buscar chamados.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChamadoDTO>> call, Throwable t) {
                // (ProgressBar.setVisibility(View.GONE) // Opcional: esconder loading)
                Log.e(TAG, "Erro de conexão ao buscar chamados.", t);
                Toast.makeText(ListaChamadosActivity.this, "Erro de conexão. Verifique a internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // --- setupTabLayout (sem alterações) ---
    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Minhas Solicitações"));
        tabLayout.addTab(tabLayout.newTab().setText("Encerrados"));

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                TextView tabTextView = new TextView(this);
                tab.setCustomView(tabTextView);
                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.setText(tab.getText());
                tabTextView.setTypeface(null, Typeface.BOLD);

                if (tab.getPosition() == 0) {
                    tabTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                } else {
                    tabTextView.setTextColor(ContextCompat.getColor(this, R.color.white_transparente_70));
                }
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView instanceof TextView) {
                    ((TextView) customView).setTextColor(ContextCompat.getColor(ListaChamadosActivity.this, android.R.color.white));
                }
                filtrarEAtualizarLista(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView instanceof TextView) {
                    ((TextView) customView).setTextColor(ContextCompat.getColor(ListaChamadosActivity.this, R.color.white_transparente_70));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // --- filtrarEAtualizarLista (ATUALIZADO para usar ChamadoDTO) ---
    private void filtrarEAtualizarLista(int position) {
        if (todosOsChamados.isEmpty()) {
            atualizarVisibilidade(new ArrayList<>(), position);
            return;
        }

        List<ChamadoDTO> listaFiltrada;

        if (position == 0) { // Minhas Solicitações (Abertos)
            listaFiltrada = todosOsChamados.stream()
                    // ✨ CORRIGIDO: Usa getDesc_status() ✨
                    .filter(c -> !"Concluído".equalsIgnoreCase(c.getDesc_status()) && !"Encerrado".equalsIgnoreCase(c.getDesc_status()))
                    .collect(Collectors.toList());
        } else { // Encerrados
            listaFiltrada = todosOsChamados.stream()
                    // ✨ CORRIGIDO: Usa getDesc_status() ✨
                    .filter(c -> "Concluído".equalsIgnoreCase(c.getDesc_status()) || "Encerrado".equalsIgnoreCase(c.getDesc_status()))
                    .collect(Collectors.toList());
        }

        chamadosAdapter.updateChamados(listaFiltrada); // ✨ CORRIGIDO: Chama o método 'updateChamados' ✨
        atualizarVisibilidade(listaFiltrada, position);
    }

    // --- atualizarVisibilidade (ATUALIZADO para usar ChamadoDTO) ---
    private void atualizarVisibilidade(List<ChamadoDTO> listaExibida, int position) {
        if (listaExibida.isEmpty()) {
            tableContainer.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            if (position == 0) { textViewEmpty.setText("Você não possui chamados em aberto"); }
            else { textViewEmpty.setText("Você não possui chamados encerrados"); }
        } else {
            tableContainer.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }

    // --- Funções do Menu (sem alterações) ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ... (seu código de inflar o menu e pintar os ícones) ...
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ... (seu código de lidar com cliques do menu) ...
        return super.onOptionsItemSelected(item);
    }
}