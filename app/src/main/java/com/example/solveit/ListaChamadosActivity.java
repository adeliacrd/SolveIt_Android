// CÓDIGO REESCRITO E CORRIGIDO PARA FORÇAR A VISUALIZAÇÃO DE ADMIN

package com.example.solveit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ListaChamadosActivity extends AppCompatActivity {

    // Declaração dos componentes da interface
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabAdicionarChamado;
    private TextView textViewAdminTitle;
    private ImageButton btnSettings;
    private ImageButton btnNotifications;
    private ImageButton btnProfile;
    private ImageButton btnAdd;
    private RecyclerView recyclerViewAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chamados);

        // ======================================================================
        // ✨✨✨ CORREÇÃO PRINCIPAL: FORÇANDO A TELA DE ADMIN ✨✨✨
        // ======================================================================
        // A linha original foi comentada para não depender mais do login.
        // boolean isUserAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        // Forçamos a variável a ser 'true' para sempre exibir a tela de admin.
        boolean isUserAdmin = true;
        // ======================================================================

        // --- Passo 2: Encontrar todos os componentes da tela ---
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fabAdicionarChamado = findViewById(R.id.fabAdicionarChamado);
        textViewAdminTitle = findViewById(R.id.textViewAdminTitle);
        btnSettings = findViewById(R.id.ic_settings);
        btnNotifications = findViewById(R.id.ic_notifications);
        btnProfile = findViewById(R.id.ic_profile);
        btnAdd = findViewById(R.id.ic_add);
        recyclerViewAdmin = findViewById(R.id.recyclerViewAdmin);

        // --- Passo 3: Lógica para configurar a tela baseada no tipo de usuário ---
        // Como 'isUserAdmin' agora é sempre 'true', o bloco 'if' sempre será executado.
        if (isUserAdmin) {
            // --- CONFIGURAÇÃO PARA O ADMINISTRADOR ---

            // Mostra os componentes exclusivos do admin
            if (textViewAdminTitle != null) textViewAdminTitle.setVisibility(View.VISIBLE);
            if (btnSettings != null) btnSettings.setVisibility(View.VISIBLE);
            if (btnAdd != null) btnAdd.setVisibility(View.VISIBLE);

            // Esconde os componentes do usuário comum
            if (tabLayout != null) tabLayout.setVisibility(View.GONE);
            if (viewPager != null) viewPager.setVisibility(View.GONE);
            if (fabAdicionarChamado != null) fabAdicionarChamado.setVisibility(View.GONE);

            // Deixa a lista do admin visível e a popula com dados
            if (recyclerViewAdmin != null) recyclerViewAdmin.setVisibility(View.VISIBLE);
            setupAdminRecyclerView();

        } else {
            // --- CONFIGURAÇÃO PARA O USUÁRIO NORMAL ---
            // Este bloco de código agora nunca será executado enquanto a linha 'isUserAdmin = true' estiver ativa.

            if (tabLayout != null) tabLayout.setVisibility(View.VISIBLE);
            if (viewPager != null) viewPager.setVisibility(View.VISIBLE);
            if (fabAdicionarChamado != null) fabAdicionarChamado.setVisibility(View.VISIBLE);

            if (textViewAdminTitle != null) textViewAdminTitle.setVisibility(View.GONE);
            if (btnSettings != null) btnSettings.setVisibility(View.GONE);
            if (btnAdd != null) btnAdd.setVisibility(View.GONE);
            if (recyclerViewAdmin != null) recyclerViewAdmin.setVisibility(View.GONE);

            setupTabsForUser();
        }

        // --- Passo 4: Configurar os cliques dos botões ---
        if (fabAdicionarChamado != null) {
            fabAdicionarChamado.setOnClickListener(v -> {
                Intent intent = new Intent(ListaChamadosActivity.this, AberturaChamadoActivity.class);
                startActivity(intent);
            });
        }

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Toast.makeText(this, "Botão de Configurações (Admin) clicado!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupTabsForUser() {
        // Método para o usuário normal, não será usado por enquanto.
    }

    /**
     * Configura o RecyclerView para a visão do administrador.
     */
    private void setupAdminRecyclerView() {
        if (recyclerViewAdmin == null) {
            return;
        }

        // Cria uma lista de dados de teste para popular a tela
        List<Chamado> listaDeTodosOsChamados = new ArrayList<>();
        listaDeTodosOsChamados.add(new Chamado(1, "PC não liga na sala de reunião", "Urgente", "Em atendimento"));
        listaDeTodosOsChamados.add(new Chamado(2, "Impressora travou o papel", "Urgente", "Aberto"));
        listaDeTodosOsChamados.add(new Chamado(3, "Monitor com listra vertical", "Urgente", "Aberto"));
        listaDeTodosOsChamados.add(new Chamado(4, "Sistema de BI está lento", "Alta", "Aberto"));
        listaDeTodosOsChamados.add(new Chamado(5, "Solicitação de novo mouse", "Alta", "Aberto"));
        listaDeTodosOsChamados.add(new Chamado(6, "Wi-Fi desconectando no 3º andar", "Média", "Aberto"));
        listaDeTodosOsChamados.add(new Chamado(7, "Erro ao salvar arquivo no servidor", "Média", "Concluído"));
        listaDeTodosOsChamados.add(new Chamado(8, "Instalação de software de edição", "Média", "Aberto"));
        listaDeTodosOsChamados.add(new Chamado(9, "Formatar notebook do novo estagiário", "Baixa", "Aberto"));
        listaDeTodosOsChamados.add(new Chamado(10, "Planilha com fórmula quebrada", "Baixa", "Concluído"));

        // Conecta o Adapter ao RecyclerView para exibir os dados
        ChamadosAdminAdapter adminAdapter = new ChamadosAdminAdapter(this, listaDeTodosOsChamados);
        recyclerViewAdmin.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdmin.setAdapter(adminAdapter);
    }
}
