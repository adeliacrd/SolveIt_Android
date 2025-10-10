package com.example.solveit;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ListaChamadosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChamados;
    private TextView textViewEmpty;
    private ChamadosAdapter chamadosAdapter;
    private List<Chamado> listaDeChamados = new ArrayList<>();
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chamados);

        // --- CONFIGURAÇÃO DA TOOLBAR ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // --- INÍCIO DA NOVA CONFIGURAÇÃO DAS ABAS (TABLAYOUT) ---
        tabLayout = findViewById(R.id.tabLayout);

        // Adiciona as abas e customiza a aparência do texto
        TabLayout.Tab tab1 = tabLayout.newTab().setText("Minhas Solicitações");
        TabLayout.Tab tab2 = tabLayout.newTab().setText("Encerrados");

        tabLayout.addTab(tab1);
        tabLayout.addTab(tab2);

        // APLICA O ESTILO CORRETO PARA CADA ABA
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab currentTab = tabLayout.getTabAt(i);
            if (currentTab != null) {
                TextView tabTextView = new TextView(this);
                currentTab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                tabTextView.setText(currentTab.getText());
                tabTextView.setTypeface(null, Typeface.BOLD);

                // Define a cor inicial: a primeira aba é branca, as outras são transparentes
                if (currentTab.getPosition() == 0) {
                    tabTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                } else {
                    tabTextView.setTextColor(ContextCompat.getColor(this, R.color.white_transparente_70));
                }
            }
        }

        // Listener para ATUALIZAR as cores quando o usuário clica nas abas
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Pega a TextView customizada da aba e muda a cor para branco (selecionada)
                View customView = tab.getCustomView();
                if (customView instanceof TextView) {
                    ((TextView) customView).setTextColor(ContextCompat.getColor(ListaChamadosActivity.this, android.R.color.white));
                }

                // Lógica para filtrar a lista (continua a mesma)
                if (tab.getPosition() == 0) {
                    Toast.makeText(ListaChamadosActivity.this, "Carregando suas solicitações...", Toast.LENGTH_SHORT).show();
                    carregarChamadosDeExemplo();
                } else if (tab.getPosition() == 1) {
                    Toast.makeText(ListaChamadosActivity.this, "Carregando chamados encerrados...", Toast.LENGTH_SHORT).show();
                    listaDeChamados.clear();
                    chamadosAdapter.notifyDataSetChanged();
                    verificarSeListaEstaVazia();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Pega a TextView da aba que perdeu a seleção e muda a cor para transparente
                View customView = tab.getCustomView();
                if (customView instanceof TextView) {
                    ((TextView) customView).setTextColor(ContextCompat.getColor(ListaChamadosActivity.this, R.color.white_transparente_70));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Não precisamos fazer nada aqui por enquanto
            }
        });
        // --- FIM DA CONFIGURAÇÃO DAS ABAS ---

        // O resto do código permanece o mesmo
        recyclerViewChamados = findViewById(R.id.recyclerViewChamados);
        textViewEmpty = findViewById(R.id.textViewEmpty);

        recyclerViewChamados.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChamados.setHasFixedSize(true);
        chamadosAdapter = new ChamadosAdapter(listaDeChamados);
        recyclerViewChamados.setAdapter(chamadosAdapter);

        carregarChamadosDeExemplo();
        verificarSeListaEstaVazia();
    }

    private void carregarChamadosDeExemplo() {
        listaDeChamados.clear();
        listaDeChamados.add(new Chamado(1, "Impressora travou no 2º andar", "Urgente", "Novo"));
        listaDeChamados.add(new Chamado(2, "PC não liga na sala de reunião", "Alta", "Novo"));
        listaDeChamados.add(new Chamado(3, "Mouse sem fio com bateria fraca", "Baixa", "Concluído"));
        listaDeChamados.add(new Chamado(4, "Sistema de ponto com erro", "Urgente", "Em atendimento"));
        listaDeChamados.add(new Chamado(5, "Solicitação de novo monitor", "Media", "Aberto"));
        listaDeChamados.add(new Chamado(6, "Wi-Fi instável no escritório", "Alta", "Novo"));
        chamadosAdapter.notifyDataSetChanged();
        verificarSeListaEstaVazia();
    }

    private void verificarSeListaEstaVazia() {
        if (listaDeChamados.isEmpty()) {
            recyclerViewChamados.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewChamados.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_notifications) {
            return true;
        } else if (id == R.id.menu_profile) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
