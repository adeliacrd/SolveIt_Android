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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    private TextView tvTempo, btnSla; // Componentes da tag de tempo/SLA
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

        // 1. Recebe os dados
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
        timelineAdapter = new TimelineAdapter(this, new ArrayList<>(), idUsuarioLogado);
        recyclerTimeline.setAdapter(timelineAdapter);

        // 4. Busca dados
        apiService = RetrofitClient.getClient().create(ApiService.class);
        buscarDetalhesChamado();

        // 5. Listeners
        btnVoltar.setOnClickListener(v -> finish());
        btnEnviarResposta.setOnClickListener(v -> enviarResposta());
        btnAssumir.setOnClickListener(v -> assumirChamado());
        btnSla.setOnClickListener(v -> Toast.makeText(this, "SLA: " + btnSla.getText(), Toast.LENGTH_SHORT).show());
    }

    private void initViews() {
        tvTituloId = findViewById(R.id.tv_detalhe_titulo_id);
        tvStatus = findViewById(R.id.tv_detalhe_status);
        tvPrioridade = findViewById(R.id.tv_detalhe_prioridade);
        tvTituloCompleto = findViewById(R.id.tv_detalhe_titulo_completo);
        tvSolicitante = findViewById(R.id.tv_detalhe_solicitante);
        tvAgente = findViewById(R.id.tv_detalhe_agente);
        tvTempo = findViewById(R.id.tv_detalhe_tempo);
        btnSla = findViewById(R.id.btn_sla);

        recyclerTimeline = findViewById(R.id.recycler_timeline);
        layoutResponder = findViewById(R.id.layout_responder);
        btnAssumir = findViewById(R.id.btn_assumir_chamado);
        btnEnviarResposta = findViewById(R.id.btn_enviar_resposta);
        editResposta = findViewById(R.id.edit_resposta);
        btnVoltar = findViewById(R.id.btn_voltar); // ID do botão de voltar
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

        // --- SOLICITANTE E EMAIL (Corrigido) ---
        String nomeSolicitante = chamadoAtual.getNome_solicitante();
        String emailSolicitante = chamadoAtual.getEmail_solicitante();

        String infoSolicitante = "Solicitante: " + nomeSolicitante;
        if (emailSolicitante != null && !emailSolicitante.isEmpty()) {
            infoSolicitante += "\n" + emailSolicitante;
        }
        tvSolicitante.setText(infoSolicitante);

        // --- AGENTE E EMAIL (Corrigido - sem "Pendente" se for nulo) ---
        String nomeAgente = chamadoAtual.getNome_agente();
        String emailAgente = chamadoAtual.getEmail_agente();

        String infoAgente = "Agente: " + (nomeAgente != null ? nomeAgente : "--"); // Mostra "--" se não for atribuído
        if (emailAgente != null && !emailAgente.isEmpty()) {
            infoAgente += "\n" + emailAgente;
        }
        tvAgente.setText(infoAgente);

        // --- 1. STATUS (TAG COLORIDA) ---
        tvStatus.setText(chamadoAtual.getDesc_status());
        int corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_default); // Padrão
        String status = chamadoAtual.getDesc_status().toLowerCase();

        if (status.contains("aberto") || status.contains("novo") || status.contains("atendimento")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_baixa); // Verde
        } else if (status.contains("transferido")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_media); // Amarelo
        } else if (status.contains("cancelado")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_urgente); // Vermelho
        }
        aplicarCorTag(tvStatus, corFundoStatus);

        // --- 2. PRIORIDADE (TAG COLORIDA) ---
        tvPrioridade.setText(chamadoAtual.getDesc_prioridade());
        int corPrioridade = ContextCompat.getColor(this, R.color.prioridade_default);
        switch (chamadoAtual.getDesc_prioridade().toLowerCase()) {
            case "urgente": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_urgente); break;
            case "alta": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_alta); break;
            case "média": case "media": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_media); break;
            case "baixa": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_baixa); break;
        }
        aplicarCorTag(tvPrioridade, corPrioridade);

        // --- 3. SLA (SEMPRE CINZA) ---
        btnSla.setText("SLA: " + (chamadoAtual.getSla_horas() > 0 ? chamadoAtual.getSla_horas() + "h" : "--"));
        aplicarCorTag(btnSla, ContextCompat.getColor(this, R.color.solveit_texto_secundario)); // Força a cor cinza

        // --- 4. TEMPO (CALCULADO) ---
        calcularTempoECorSla(); // Este método cuida da cor e texto do tvTempo

        // --- 5. TIMELINE (COM DESCRIÇÃO INICIAL) ---
        List<InteracaoDTO> listaCompleta = new ArrayList<>();

        // ✨ LOG DE VERIFICAÇÃO FINAL ✨
        Log.d(TAG, "ID CRIADOR (DTO): " + chamadoAtual.getId_usuario());
        Log.d(TAG, "ID LOGADO (PREFS): " + idUsuarioLogado);
        Log.d(TAG, "Resultado da Comparação: " + (chamadoAtual.getId_usuario() == idUsuarioLogado));

        // Cria o DTO falso para a descrição inicial (A LINHA QUE DÁ PROBLEMA)
        InteracaoDTO descricaoInicial = new InteracaoDTO(
                0,
                idChamado,
                chamadoAtual.getId_usuario(), // ESTE É O ID DO CRIADOR
                chamadoAtual.getNome_solicitante(),
                chamadoAtual.getDesc_chamado(),
                chamadoAtual.getDt_abertura()
        );
        listaCompleta.add(descricaoInicial);

        if (chamadoAtual.getTimeline() != null) {
            listaCompleta.addAll(chamadoAtual.getTimeline());
        }
        timelineAdapter.atualizarLista(listaCompleta);
    }

    // (O método auxiliar aplicarCorTag e calcularTempoECorSla já estão corretos)

    // Método auxiliar para aplicar cor ao background arredondado
    private void aplicarCorTag(TextView view, int cor) {
        view.setTextColor(Color.WHITE);
        GradientDrawable background = (GradientDrawable) view.getBackground().mutate();
        background.setColor(cor);
    }

    // Calcula o tempo decorrido e define a cor da tag de Tempo
    private void calcularTempoECorSla() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

            if (chamadoAtual.getDt_abertura() == null) { tvTempo.setText("--"); return; }

            Date dataAbertura = sdf.parse(chamadoAtual.getDt_abertura());
            Date dataReferencia;

            boolean isConcluido = chamadoAtual.getDesc_status().toLowerCase().contains("concluído")
                    || chamadoAtual.getDesc_status().toLowerCase().contains("cancelado");

            if (isConcluido && chamadoAtual.getDt_fechamento() != null) {
                dataReferencia = sdf.parse(chamadoAtual.getDt_fechamento());
            } else {
                dataReferencia = new Date(); // Agora
            }

            long diffMillis = dataReferencia.getTime() - dataAbertura.getTime();
            long horasCorridas = TimeUnit.MILLISECONDS.toHours(diffMillis);

            long dias = TimeUnit.MILLISECONDS.toDays(diffMillis);
            long horas = horasCorridas % 24;
            long minutos = TimeUnit.MILLISECONDS.toMinutes(diffMillis) % 60;

            tvTempo.setText(String.format(Locale.getDefault(), "%dd %02dh %02dm", dias, horas, minutos));

            // Lógica de Cor do SLA
            int slaLimiteHoras = chamadoAtual.getSla_horas();
            long totalHorasCorridas = TimeUnit.MILLISECONDS.toHours(diffMillis);

            int corTempo;

            if (isConcluido) {
                corTempo = ContextCompat.getColor(this, R.color.prioridade_default);
            } else if (slaLimiteHoras > 0) {
                if (totalHorasCorridas >= slaLimiteHoras) {
                    corTempo = ContextCompat.getColor(this, R.color.prioridade_urgente); // Vermelho
                } else if (totalHorasCorridas >= (slaLimiteHoras / 2.0)) {
                    corTempo = ContextCompat.getColor(this, R.color.prioridade_media); // Amarelo
                } else {
                    corTempo = ContextCompat.getColor(this, R.color.prioridade_baixa); // Verde
                }
            } else {
                corTempo = ContextCompat.getColor(this, R.color.prioridade_default); // Sem SLA
            }
            aplicarCorTag(tvTempo, corTempo);

        } catch (Exception e) {
            tvTempo.setText("--");
            aplicarCorTag(tvTempo, ContextCompat.getColor(this, R.color.prioridade_default));
        }
    }

    private void configurarVisibilidadeBotoes() {
        boolean isAgente = (idTipoAcessoLogado == 2);
        boolean isAdm = (idTipoAcessoLogado == 3);
        boolean isCliente = (idTipoAcessoLogado == 1);
        boolean temAgenteAtribuido = (chamadoAtual.getNome_agente() != null);

        // Esconde tudo por padrão
        layoutResponder.setVisibility(View.GONE);
        btnAssumir.setVisibility(View.GONE);

        if (isCliente) {
            layoutResponder.setVisibility(View.VISIBLE);
        }
        else if (isAgente) {
            if (!temAgenteAtribuido) {
                btnAssumir.setVisibility(View.VISIBLE);
            } else {
                layoutResponder.setVisibility(View.VISIBLE);
            }
        }
        else if (isAdm) {
            layoutResponder.setVisibility(View.VISIBLE);
        }
    }

    private void enviarResposta() {
        // Lógica para chamar o ComentarioServlet (POST)
        Toast.makeText(this, "Enviar resposta: Em breve...", Toast.LENGTH_SHORT).show();
    }

    private void assumirChamado() {
        // Lógica para chamar o endpoint de atribuir (UPDATE)
        Toast.makeText(this, "Assumir chamado: Em breve...", Toast.LENGTH_SHORT).show();
    }
}