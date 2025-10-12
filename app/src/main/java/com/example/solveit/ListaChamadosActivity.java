package com.example.solveit;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable; // IMPORT ADICIONADO
import android.os.Bundle;
import android.view.Menu;import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat; // IMPORT ADICIONADO
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaChamadosActivity extends AppCompatActivity {

    // --- Variáveis da Classe (sem alterações) ---
    private RecyclerView recyclerViewChamados;
    private ChamadosAdapter chamadosAdapter;
    private TextView textViewEmpty;
    private View tableContainer;
    private TabLayout tabLayout;
    private final List<Chamado> todosOsChamados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chamados);

        // --- Configuração dos Componentes (sem alterações) ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        recyclerViewChamados = findViewById(R.id.recyclerViewChamados);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        tableContainer = findViewById(R.id.tableContainer);
        tabLayout = findViewById(R.id.tabLayout);

        // --- Carregar Dados, Configurar RecyclerView e Abas (sem alterações) ---
        carregarChamadosDeExemplo();
        recyclerViewChamados.setLayoutManager(new LinearLayoutManager(this));
        chamadosAdapter = new ChamadosAdapter(new ArrayList<>());
        recyclerViewChamados.setAdapter(chamadosAdapter);
        setupTabLayout();

        // --- Exibição Inicial (sem alterações) ---
        tabLayout.getTabAt(0).select();
        filtrarEAtualizarLista(0);
    }

    // --- carregarChamadosDeExemplo (sem alterações) ---
    private void carregarChamadosDeExemplo() {
        todosOsChamados.clear();
        todosOsChamados.add(new Chamado(1, "Impressora travou no 2º andar", "Urgente", "Novo"));
        todosOsChamados.add(new Chamado(2, "PC não liga na sala de reunião", "Alta", "Novo"));
        todosOsChamados.add(new Chamado(3, "Mouse sem fio com bateria fraca", "Baixa", "Concluído"));
        todosOsChamados.add(new Chamado(4, "Sistema de ponto com erro", "Urgente", "Em atendimento"));
        todosOsChamados.add(new Chamado(5, "Solicitação de novo monitor", "Media", "Aberto"));
        todosOsChamados.add(new Chamado(6, "Wi-Fi instável no escritório", "Alta", "Novo"));
        todosOsChamados.add(new Chamado(7, "Troca de teclado", "Baixa", "Concluído"));
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
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    // --- filtrarEAtualizarLista (sem alterações) ---
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

    // --- atualizarVisibilidade (sem alterações) ---
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

    // --- Funções do Menu ---
    // =============================================================
    // ✨ MÉTODO ATUALIZADO PARA COLORIR OS ÍCONES ✨
    // =============================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 1. Infla o menu a partir do XML.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // 2. Itera sobre cada item do menu para aplicar a cor.
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable drawable = menuItem.getIcon();

            // 3. Se o item tiver um ícone, aplica o filtro de cor branco.
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable); // Prepara o ícone para ser modificado
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white)); // Pinta de branco
                menuItem.setIcon(drawable); // Devolve o ícone pintado ao menu
            }
        }

        return true;
    }

    // --- onOptionsItemSelected (sem alterações) ---
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_notifications || id == R.id.menu_profile) {
            // Ação para os ícones pode ser adicionada aqui no futuro
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
