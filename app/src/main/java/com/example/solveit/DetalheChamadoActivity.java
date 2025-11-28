package com.example.solveit;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveit.api.ApiService;
import com.example.solveit.api.ChamadoCompletoDTO;
import com.example.solveit.api.InteracaoDTO;
import com.example.solveit.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalheChamadoActivity extends AppCompatActivity {

    private static final String TAG = "DetalheChamado";
    private int idChamado;
    private int idUsuarioLogado;
    private int idTipoAcessoLogado;

    // UI Components
    private TextView tvTituloId, tvStatus, tvPrioridade, tvTituloCompleto, tvSolicitante, tvAgente;
    private RecyclerView recyclerTimeline;
    private TimelineAdapter timelineAdapter;
    private LinearLayout layoutResponder;
    private Button btnAssumir, btnEnviarResposta;
    private EditText editResposta;
    private ImageButton btnVoltar;

    private ApiService apiService;
    private ChamadoCompletoDTO chamadoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_chamado);

        // 1. Recebe os dados do Intent e SharedPreferences
        idChamado = getIntent().getIntExtra("ID_CHAMADO", -1);
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        idUsuarioLogado = prefs.getInt(MainActivity.KEY_USER_ID, -1);
        idTipoAcessoLogado = prefs.getInt(MainActivity.KEY_USER_ROLE_ID, -1);

        if (idChamado == -1) {
            Toast.makeText(this, "Erro ao abrir chamado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Inicializa UI
        initViews();

        // 3. Configura RecyclerView da Timeline
        recyclerTimeline.setLayoutManager(new LinearLayoutManager(this));
        // Passa o ID logado para o adapter saber alinhar as mensagens
        timelineAdapter = new TimelineAdapter(this, new ArrayList<>(), idUsuarioLogado);
        recyclerTimeline.setAdapter(timelineAdapter);

        // 4. Busca dados da API
        apiService = RetrofitClient.getClient().create(ApiService.class);
        buscarDetalhesChamado();

        // 5. Listeners
        btnVoltar.setOnClickListener(v -> finish());
        btnEnviarResposta.setOnClickListener(v -> enviarResposta());
        btnAssumir.setOnClickListener(v -> assumirChamado());
    }

    private void initViews() {
        tvTituloId = findViewById(R.id.tv_detalhe_titulo_id);
        tvStatus = findViewById(R.id.tv_detalhe_status);
        tvPrioridade = findViewById(R.id.tv_detalhe_prioridade);
        tvTituloCompleto = findViewById(R.id.tv_detalhe_titulo_completo);
        tvSolicitante = findViewById(R.id.tv_detalhe_solicitante);
        tvAgente = findViewById(R.id.tv_detalhe_agente);
        recyclerTimeline = findViewById(R.id.recycler_timeline);

        layoutResponder = findViewById(R.id.layout_responder);
        btnAssumir = findViewById(R.id.btn_assumir_chamado);
        btnEnviarResposta = findViewById(R.id.btn_enviar_resposta);
        editResposta = findViewById(R.id.edit_resposta);
        btnVoltar = findViewById(R.id.btn_voltar);
    }

    private void buscarDetalhesChamado() {
        Call<ChamadoCompletoDTO> call = apiService.getDetalhesChamado(idChamado);
        call.enqueue(new Callback<ChamadoCompletoDTO>() {
            @Override
            public void onResponse(Call<ChamadoCompletoDTO> call, Response<ChamadoCompletoDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chamadoAtual = response.body();
                    preencherDadosNaTela();
                    configurarVisibilidadeBotoes();
                } else {
                    Toast.makeText(DetalheChamadoActivity.this, "Erro ao carregar detalhes.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ChamadoCompletoDTO> call, Throwable t) {
                Toast.makeText(DetalheChamadoActivity.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherDadosNaTela() {
        tvTituloId.setText("Chamado (ID " + chamadoAtual.getId_chamado() + ")");
        tvTituloCompleto.setText(chamadoAtual.getTitulo());
        tvSolicitante.setText("Solicitante: " + chamadoAtual.getNome_solicitante());

        String nomeAgente = chamadoAtual.getNome_agente();
        tvAgente.setText("Agente: " + (nomeAgente != null ? nomeAgente : "Pendente"));

        // --- 1. STATUS (TAG COLORIDA) ---
        tvStatus.setText(chamadoAtual.getDesc_status());

        int corFundoStatus;
        String status = chamadoAtual.getDesc_status().toLowerCase();

        if (status.contains("aberto") || status.contains("novo")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_baixa); // Verde
        } else if (status.contains("atendimento")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_baixa); // Verde
        } else if (status.contains("transferido")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_media); // Amarelo
        } else if (status.contains("cancelado")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_urgente); // Vermelho
        } else {
            // Concluído ou outros
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_default); // Cinza
        }

        // Aplica a cor ao "molde" arredondado (drawable/bg_rounded_tag)
        // Certifique-se de que no XML o tvStatus tem android:background="@drawable/bg_rounded_tag"
        GradientDrawable backgroundStatus = (GradientDrawable) tvStatus.getBackground();
        backgroundStatus.setColor(corFundoStatus);

        // Texto Branco
        tvStatus.setTextColor(Color.WHITE);

        tvPrioridade.setText(chamadoAtual.getDesc_prioridade());
        // Lógica de Cor da Prioridade
        int corPrioridade = ContextCompat.getColor(this, R.color.prioridade_default);
        switch (chamadoAtual.getDesc_prioridade().toLowerCase()) {
            case "urgente": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_urgente); break;
            case "alta": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_alta); break;
            case "média": case "media": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_media); break;
            case "baixa": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_baixa); break;
        }
        ((GradientDrawable) tvPrioridade.getBackground()).setColor(corPrioridade);

        // Preenche a Timeline
        // Adiciona a descrição inicial como a primeira "mensagem"
        List<InteracaoDTO> listaComDescricao = new ArrayList<>();

        // Cria um DTO falso para a descrição inicial (para aparecer no chat)
        // (Isso é um truque visual útil)
        // InteracaoDTO descricaoInicial = new InteracaoDTO(0, idChamado, ???, chamadoAtual.getNome_solicitante(), chamadoAtual.getDesc_chamado(), chamadoAtual.getDt_abertura());
        // listaComDescricao.add(descricaoInicial);

        if (chamadoAtual.getTimeline() != null) {
            listaComDescricao.addAll(chamadoAtual.getTimeline());
        }
        timelineAdapter.atualizarLista(listaComDescricao);
    }

    // ==========================================================
    // ✨ LÓGICA DE QUEM PODE FAZER O QUÊ ✨
    // ==========================================================
    private void configurarVisibilidadeBotoes() {
        boolean isAgente = (idTipoAcessoLogado == 2);
        boolean isAdm = (idTipoAcessoLogado == 3);
        boolean isCliente = (idTipoAcessoLogado == 1);
        boolean temAgenteAtribuido = (chamadoAtual.getNome_agente() != null);

        // Esconde tudo por padrão
        layoutResponder.setVisibility(View.GONE);
        btnAssumir.setVisibility(View.GONE);

        if (isCliente) {
            // Cliente pode responder se o chamado NÃO estiver fechado
            layoutResponder.setVisibility(View.VISIBLE);
        }
        else if (isAgente) {
            if (!temAgenteAtribuido) {
                // Agente vê botão ASSUMIR se ninguém pegou ainda
                btnAssumir.setVisibility(View.VISIBLE);
            } else {
                // Se já tem agente, verifica se sou EU
                // (Precisamos do ID do agente no DTO para essa verificação precisa,
                //  por enquanto assumimos que se tem agente, ele pode responder se for ele)
                layoutResponder.setVisibility(View.VISIBLE);
            }
        }
        else if (isAdm) {
            // ADM pode tudo (Responder e Atribuir - atribuir faremos depois)
            layoutResponder.setVisibility(View.VISIBLE);
        }
    }

    private void enviarResposta() {
        // Lógica para chamar o ComentarioServlet (POST)
        // ...
    }

    private void assumirChamado() {
        // Lógica para chamar o endpoint de atribuir (UPDATE)
        // ...
    }
}
