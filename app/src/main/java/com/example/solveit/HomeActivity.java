package com.example.solveit;

// Imports necessários
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout; // Importar LinearLayout
import android.widget.TextView; // Importar TextView
import android.widget.Toast;

import androidx.annotation.NonNull; // Importar
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout; // Importar TabLayout

import com.example.solveit.api.ApiService;
import com.example.solveit.api.ChamadoDTO;
import com.example.solveit.api.RetrofitClient;

import java.io.IOException; // Importar
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Importar

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private int idUsuarioLogado;
    private int idTipoAcessoLogado;

    // --- Componentes da UI ---
    private ImageButton iconConfig, iconAdd, iconNotificacoes, iconPerfil;
    private RecyclerView recyclerViewChamados;
    private ChamadosAdapter chamadosAdapter;
    private List<ChamadoDTO> listaDeChamadosCompleta = new ArrayList<>(); // Lista com TODOS os dados

    private TabLayout tabLayout;
    private LinearLayout layoutCabecalhoAdm;
    private TextView textViewEmpty; // ✨ VARIÁVEL PARA O TEXTO DE LISTA VAZIA ✨

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- 1. Lê os dados do usuário ---
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        idUsuarioLogado = prefs.getInt(MainActivity.KEY_USER_ID, -1);
        idTipoAcessoLogado = prefs.getInt(MainActivity.KEY_USER_ROLE_ID, -1);
        Log.d(TAG, "Tela Home iniciada por: ID " + idUsuarioLogado + ", Nível " + idTipoAcessoLogado);

        // --- 2. Configura a Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar_icones);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // --- 3. Conecta os Ícones do XML com o Java ---
        iconConfig = findViewById(R.id.icon_config);
        iconAdd = findViewById(R.id.icon_add);
        iconNotificacoes = findViewById(R.id.ic_notifications);
        iconPerfil = findViewById(R.id.btn_profile);
        configurarIconesToolbar();

        // --- 4. Encontra os componentes que trocam ---
        tabLayout = findViewById(R.id.tabLayout_home);
        layoutCabecalhoAdm = findViewById(R.id.layout_cabecalho_adm);

        // ✨ CONECTA O TEXTO DE LISTA VAZIA ✨
        textViewEmpty = findViewById(R.id.textViewEmpty);
        if (textViewEmpty == null) {
            Log.e(TAG, "FATAL: TextView com ID 'textViewEmpty' não foi encontrado no layout! A mensagem de lista vazia não funcionará.");
        }

        // --- 5. Configura o RecyclerView ---
        recyclerViewChamados = findViewById(R.id.recyclerView_adm_chamados);
        recyclerViewChamados.setLayoutManager(new LinearLayoutManager(this));
        chamadosAdapter = new ChamadosAdapter(this, new ArrayList<>());
        recyclerViewChamados.setAdapter(chamadosAdapter);

        chamadosAdapter.setOnItemClickListener(chamadoClicado -> {
            // PASSO 3: MUDANÇA DE DESTINO
            // A Intent agora aponta para a nova tela que criamos.
            Intent intent = new Intent(HomeActivity.this, ConversaChamadoActivity.class);

            // Por enquanto, vamos enviar o ID do chamado. A nova tela usará esse ID
            // para buscar todos os detalhes da API no futuro.
            intent.putExtra("CHAMADO_ID", chamadoClicado.getId_chamado());

            // Inicia a nova tela
            startActivity(intent);
        });



        // --- 6. A MÁGICA: Decide o layout com base no acesso ---
        if (idTipoAcessoLogado == 3) { // 3 = ADM
            prepararTelaAdm();
        } else if (idTipoAcessoLogado == 1) { // 1 = Cliente
            prepararTelaCliente();
        } else if (idTipoAcessoLogado == 2) { // 2 = Agente
            prepararTelaAgente();
        } else {
            Toast.makeText(this, "Erro: Nível de acesso desconhecido.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    // --- Métodos de Configuração de UI ---
    private void configurarIconesToolbar() {
        iconConfig.setVisibility(idTipoAcessoLogado == 3 ? View.VISIBLE : View.GONE);
        iconConfig.setOnClickListener(v -> Toast.makeText(this, "Configurações (ADM) clicado!", Toast.LENGTH_SHORT).show());
        iconAdd.setOnClickListener(v -> startActivity(new Intent(this, AberturaChamadoActivity.class)));
// ✨ Linha NOVA E CORRETA ✨
        iconNotificacoes.setOnClickListener(v -> startActivity(new Intent(this, NotificacoesActivity.class)));        // ✨ Linha NOVA E CORRETA ✨
        iconPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));

    }

    private void prepararTelaAdm() {
        Log.d(TAG, "Preparando UI para ADM...");
        tabLayout.setVisibility(View.GONE);
        layoutCabecalhoAdm.setVisibility(View.VISIBLE);
    }

    private void prepararTelaCliente() {
        Log.d(TAG, "Preparando UI para Cliente...");
        layoutCabecalhoAdm.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);

        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Meus Chamados"));
        tabLayout.addTab(tabLayout.newTab().setText("Encerrados"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { filtrarListaParaCliente(tab.getPosition()); }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void prepararTelaAgente() {
        Log.d(TAG, "Preparando UI para Agente...");
        layoutCabecalhoAdm.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Disponíveis"));
        tabLayout.addTab(tabLayout.newTab().setText("Meus Atendimentos"));
    }

    // --- Métodos de Busca de Dados ---
    private void buscarChamadosDaApi() {
        Log.d(TAG, "Buscando chamados da API...");
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<ChamadoDTO>> call = apiService.getChamados();

        call.enqueue(new Callback<List<ChamadoDTO>>() {
            @Override
            public void onResponse(Call<List<ChamadoDTO>> call, Response<List<ChamadoDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaDeChamadosCompleta.clear();
                    listaDeChamadosCompleta.addAll(response.body());
                    Log.d(TAG, "Chamados carregados: " + listaDeChamadosCompleta.size());

                    // Decide como filtrar a lista que acabou de chegar
                    if (idTipoAcessoLogado == 3) { // ADM
                        chamadosAdapter.updateChamados(listaDeChamadosCompleta);
                        atualizarVisibilidadeLista(listaDeChamadosCompleta, "Nenhum chamado no sistema.");
                    } else if (idTipoAcessoLogado == 1) { // Cliente
                        filtrarListaParaCliente(tabLayout.getSelectedTabPosition());
                    } else if (idTipoAcessoLogado == 2) { // Agente
                        chamadosAdapter.updateChamados(listaDeChamadosCompleta); // (Placeholder)
                        atualizarVisibilidadeLista(listaDeChamadosCompleta, "Nenhum chamado disponível."); // (Placeholder)
                    }
                } else {
                    String errorMsg = "Falha ao buscar chamados. Código: " + response.code();
                    try { if(response.errorBody() != null) errorMsg += " - " + response.errorBody().string(); } catch (IOException e) { e.printStackTrace(); }
                    Log.e(TAG, errorMsg);
                    Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    // Mostra erro na lista
                    atualizarVisibilidadeLista(new ArrayList<>(), "Falha ao carregar chamados.");
                }
            }
            @Override
            public void onFailure(Call<List<ChamadoDTO>> call, Throwable t) {
                Log.e(TAG, "Erro de rede ao buscar chamados: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Erro de conexão ao buscar chamados.", Toast.LENGTH_LONG).show();
                // Mostra erro na lista
                atualizarVisibilidadeLista(new ArrayList<>(), "Erro de conexão. Verifique a internet.");
            }
        });
    }

    // --- Lógica de Filtro para Cliente ---
    private void filtrarListaParaCliente(int tabPosition) {
        if (listaDeChamadosCompleta.isEmpty()) {
            atualizarVisibilidadeLista(new ArrayList<>(), "Nenhum chamado encontrado.");
            return;
        }

        List<ChamadoDTO> listaFiltrada;

        // 1. Filtra por "MEUS" chamados primeiro
        // ✨ ATENÇÃO: ISSO SÓ VAI FUNCIONAR DEPOIS QUE ATUALIZARMOS O BACKEND (Passo 4) ✨
        List<ChamadoDTO> meusChamados = listaDeChamadosCompleta.stream()
                .filter(c -> c.getId_usuario() == idUsuarioLogado) // <-- O FILTRO CHAVE
                .collect(Collectors.toList());

        String msgVazio;
        // 2. Filtra por STATUS (Abertos vs. Encerrados)
        if (tabPosition == 0) { // Aba "Meus Chamados" (Abertos)
            listaFiltrada = meusChamados.stream()
                    .filter(c -> !"Concluído".equalsIgnoreCase(c.getDesc_status()) && !"Cancelado".equalsIgnoreCase(c.getDesc_status()))
                    .collect(Collectors.toList());
            msgVazio = "Você não possui chamados em aberto.";
        } else { // Aba "Encerrados"
            listaFiltrada = meusChamados.stream()
                    .filter(c -> "Concluído".equalsIgnoreCase(c.getDesc_status()) || "Cancelado".equalsIgnoreCase(c.getDesc_status()))
                    .collect(Collectors.toList());
            msgVazio = "Você não possui chamados encerrados.";
        }

        chamadosAdapter.updateChamados(listaFiltrada);
        atualizarVisibilidadeLista(listaFiltrada, msgVazio);
    }

    // --- ✨ MÉTODO CORRIGIDO: Para mostrar/esconder a lista ✨ ---
    private void atualizarVisibilidadeLista(List<ChamadoDTO> listaExibida, String mensagemVazio) {

        // A variável textViewEmpty foi conectada no onCreate
        if (textViewEmpty == null) {
            // Se o findViewById falhou, não podemos continuar
            Log.e(TAG, "TextView 'textViewEmpty' é nulo. Não é possível atualizar a visibilidade.");
            return;
        }

        if (listaExibida == null || listaExibida.isEmpty()) {
            recyclerViewChamados.setVisibility(View.GONE); // Esconde a lista
            textViewEmpty.setVisibility(View.VISIBLE); // Mostra a mensagem
            textViewEmpty.setText(mensagemVazio);
        } else {
            recyclerViewChamados.setVisibility(View.VISIBLE); // Mostra a lista
            textViewEmpty.setVisibility(View.GONE); // Esconde a mensagem
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Recarrega a lista toda vez que a tela volta
        buscarChamadosDaApi();
    }
}