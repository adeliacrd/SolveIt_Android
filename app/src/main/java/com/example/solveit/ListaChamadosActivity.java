package com.example.solveit;

// IMPORTAÇÕES NECESSÁRIAS (ADICIONADAS)
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// SUAS IMPORTAÇÕES EXISTENTES
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaChamadosActivity extends AppCompatActivity {

    // --- Suas variáveis de classe (sem alterações) ---
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
        Toolbar toolbar = findViewById(R.id.toolbar_icones);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        recyclerViewChamados = findViewById(R.id.recyclerViewChamados);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        tableContainer = findViewById(R.id.tableContainer);
        tabLayout = findViewById(R.id.tabLayout);

        // --- Dados de Exemplo (Corretamente comentados) ---
        // carregarChamadosDeExemplo();

        // --- Configuração do RecyclerView e Adapter (sem alterações) ---
        recyclerViewChamados.setLayoutManager(new LinearLayoutManager(this));
        chamadosAdapter = new ChamadosAdapter(new ArrayList<>());
        recyclerViewChamados.setAdapter(chamadosAdapter);

        // --- Configuração das Abas (sem alterações) ---
        setupTabLayout();

        // --- Exibição Inicial (sem alterações) ---
        tabLayout.getTabAt(0).select();
        filtrarEAtualizarLista(0);


        // ================================================================
        // ✨ CÓDIGO ADICIONADO PARA FAZER O BOTÃO FUNCIONAR ✨
        // Configura o listener de clique para o FloatingActionButton (FAB).
        // ================================================================
        FloatingActionButton fabAdicionarChamado = findViewById(R.id.fabAdicionarChamado);
        fabAdicionarChamado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cria uma "Intenção" de navegar da tela atual para a tela de Abertura de Chamado.
                Intent intent = new Intent(ListaChamadosActivity.this, AberturaChamadoActivity.class);
                // Executa a navegação.
                startActivity(intent);
            }
        });
        // ================================================================

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

    // --- Funções do Menu (sem alterações) ---
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ic_notifications || id == R.id.btn_profile) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
