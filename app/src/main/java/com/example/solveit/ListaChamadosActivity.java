package com.example.solveit;

// IMPORTAÇÕES NECESSÁRIAS
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveit.api.ApiService;
import com.example.solveit.api.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class ListaChamadosActivity extends AppCompatActivity {

    // --- Suas variáveis de classe (sem alterações) ---
    private RecyclerView recyclerViewChamados;
    private ChamadosAdapter chamadosAdapter;
    private TextView textViewEmpty;
    private View tableContainer;
    private TabLayout tabLayout;
    private final List<Chamado> todosOsChamados = new ArrayList<>();
    private static final String TAG = "ListaChamadosActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chamados);

        // --- Configuração da UI (sem alterações) ---
        Toolbar toolbar = findViewById(R.id.toolbar_icones);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        recyclerViewChamados = findViewById(R.id.recyclerViewChamados);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        tableContainer = findViewById(R.id.tableContainer);
        tabLayout = findViewById(R.id.tabLayout);

        recyclerViewChamados.setLayoutManager(new LinearLayoutManager(this));
        chamadosAdapter = new ChamadosAdapter(new ArrayList<>());
        recyclerViewChamados.setAdapter(chamadosAdapter);

        setupTabLayout();

        FloatingActionButton fabAdicionarChamado = findViewById(R.id.fabAdicionarChamado);
        fabAdicionarChamado.setOnClickListener(view -> {
            Intent intent = new Intent(ListaChamadosActivity.this, AberturaChamadoActivity.class);
            startActivity(intent);
        });

        // --- Inicia o carregamento dos dados REAIS da API ---
        carregarDadosDaApi();
    }

    private void carregarDadosDaApi() {
        Toast.makeText(this, "Buscando chamados...", Toast.LENGTH_SHORT).show();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            final List<Chamado> chamadosBuscados = new ArrayList<>();
            final String[] erro = {null};

            try {
                // --- Bloco de chamada à API (sem alterações) ---
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                Call<List<Chamado>> call = apiService.getChamados();
                Response<List<Chamado>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    chamadosBuscados.addAll(response.body());
                } else {
                    erro[0] = "Falha ao buscar dados. Código: " + response.code();
                    Log.e(TAG, erro[0]);
                }
            } catch (Exception e) {
                erro[0] = "Erro de conexão. Verifique a URL e a internet.";
                Log.e(TAG, erro[0], e);
            }

            // --- Bloco para atualizar a tela ---
            runOnUiThread(() -> {
                todosOsChamados.clear();
                todosOsChamados.addAll(chamadosBuscados);
                filtrarEAtualizarLista(tabLayout.getSelectedTabPosition());

                // =====================================================================
                // ✨ AQUI ESTÁ A CORREÇÃO ✨
                // O Toast só é exibido se a tela ainda estiver ativa para o usuário.
                // =====================================================================
                if (!isFinishing() && !isDestroyed()) {
                    if (erro[0] != null) {
                        Toast.makeText(ListaChamadosActivity.this, erro[0], Toast.LENGTH_LONG).show();
                    } else {
                        // Opcional: Você pode querer remover a mensagem de sucesso para não poluir a tela.
                        // Toast.makeText(ListaChamadosActivity.this, "Chamados carregados!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    // --- O RESTO DOS SEUS MÉTODOS ESTÁ CORRETO E NÃO PRECISA DE ALTERAÇÃO ---

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
                tabTextView.setTextSize(18);

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

    private void filtrarEAtualizarLista(int position) {
        List<Chamado> listaFiltrada;

        if (position == 0) {
            listaFiltrada = todosOsChamados.stream()
                    .filter(c -> !c.getStatus().equals("Concluído"))
                    .collect(Collectors.toList());
        } else {
            listaFiltrada = todosOsChamados.stream()
                    .filter(c -> c.getStatus().equals("Concluído"))
                    .collect(Collectors.toList());
        }

        chamadosAdapter.atualizarLista(listaFiltrada);
        atualizarVisibilidade(listaFiltrada, position);
    }

    private void atualizarVisibilidade(List<Chamado> listaExibida, int position) {
        if (listaExibida.isEmpty()) {
            tableContainer.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);

            if (position == 0) {
                textViewEmpty.setText("Você não possui chamados em aberto");
            } else {
                textViewEmpty.setText("Você não possui chamados encerrados");
            }
        } else {
            tableContainer.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable drawable = menuItem.getIcon();
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
                menuItem.setIcon(drawable);
            }
        }
        return true;
    }

    // ✨ Atenção: se você aplicou a correção de renomear os IDs, eles devem estar diferentes aqui.
    // Este código usa os IDs que você me mandou por último.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ic_notifications) { // Verifique se este é o ID correto do seu main_menu.xml
            Intent intent = new Intent(ListaChamadosActivity.this, NotificacoesActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.btn_profile) { // Verifique se este é o ID correto do seu main_menu.xml
            Toast.makeText(this, "Tela de perfil será aberta aqui.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
