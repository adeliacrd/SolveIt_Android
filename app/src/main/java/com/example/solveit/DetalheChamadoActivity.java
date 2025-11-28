package com.example.solveit;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
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
import com.example.solveit.api.AtribuicaoResponse;
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
    private TextView tvTempo, btnSla;
    private RecyclerView recyclerTimeline;
    private TimelineAdapter timelineAdapter;
    private LinearLayout layoutResponder;
    private Button btnAssumir, btnEnviarResposta, btnConcluir, btnCancelar, btnAtribuir, btnEncerrar;
    private EditText editResposta;
    private ImageButton btnVoltar;
    private TextView btnAnexarChat;
    // ✨ ADICIONE ESTA LINHA ✨
    private TextView tvIniciaisResponder;

    private ApiService apiService;
    private ChamadoCompletoDTO chamadoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_chamado);

        idChamado = getIntent().getIntExtra("ID_CHAMADO", -1);
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        idUsuarioLogado = prefs.getInt(MainActivity.KEY_USER_ID, -1);
        idTipoAcessoLogado = prefs.getInt(MainActivity.KEY_USER_ROLE_ID, -1);

        if (idChamado == -1) {
            Toast.makeText(this, "Erro ao abrir chamado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();

        recyclerTimeline.setLayoutManager(new LinearLayoutManager(this));
        timelineAdapter = new TimelineAdapter(this, new ArrayList<>(), idUsuarioLogado);
        recyclerTimeline.setAdapter(timelineAdapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        buscarDetalhesChamado();

        btnVoltar.setOnClickListener(v -> finish());
        btnEnviarResposta.setOnClickListener(v -> enviarResposta());
        btnAssumir.setOnClickListener(v -> assumirChamado());
        btnSla.setOnClickListener(v -> Toast.makeText(this, "SLA: " + btnSla.getText(), Toast.LENGTH_SHORT).show());

        // Listeners dos botões de ação (se visíveis)
        btnConcluir.setOnClickListener(v -> concluirChamado());
        btnCancelar.setOnClickListener(v -> cancelarChamado());
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

        // Botões
        btnAssumir = findViewById(R.id.btn_assumir_chamado);
        btnEnviarResposta = findViewById(R.id.btn_enviar_resposta);
        btnConcluir = findViewById(R.id.btn_concluir_chamado);
        btnCancelar = findViewById(R.id.btn_cancelar_chamado);
        btnAtribuir = findViewById(R.id.btn_atribuir_chamado);
        btnEncerrar = findViewById(R.id.btn_encerrar_extra);

        editResposta = findViewById(R.id.edit_resposta);
        btnVoltar = findViewById(R.id.btn_voltar); // ID corrigido
        btnAnexarChat = findViewById(R.id.btn_anexar_chat);

        if (btnAnexarChat != null) {
            String textoAnexo = "Para anexar arquivos, <font color='#1976D2'>clique aqui</font>";
            btnAnexarChat.setText(android.text.Html.fromHtml(textoAnexo, android.text.Html.FROM_HTML_MODE_LEGACY));
        }
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

        String infoSolicitante = "Solicitante: " + chamadoAtual.getNome_solicitante();
        if (chamadoAtual.getEmail_solicitante() != null && !chamadoAtual.getEmail_solicitante().isEmpty()) {
            infoSolicitante += "\n" + chamadoAtual.getEmail_solicitante();
        }
        tvSolicitante.setText(infoSolicitante);

        String nomeAgente = chamadoAtual.getNome_agente();
        String infoAgente = "Agente: " + (nomeAgente != null ? nomeAgente : "--");
        if (chamadoAtual.getEmail_agente() != null && !chamadoAtual.getEmail_agente().isEmpty()) {
            infoAgente += "\n" + chamadoAtual.getEmail_agente();
        }
        tvAgente.setText(infoAgente);

        // --- STATUS ---
        tvStatus.setText(chamadoAtual.getDesc_status());
        int corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_default);
        String status = chamadoAtual.getDesc_status().toLowerCase();
        if (status.contains("aberto") || status.contains("novo") || status.contains("atendimento")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_baixa);
        } else if (status.contains("transferido")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_media);
        } else if (status.contains("cancelado")) {
            corFundoStatus = ContextCompat.getColor(this, R.color.prioridade_urgente);
        }
        aplicarCorTag(tvStatus, corFundoStatus);

        // --- PRIORIDADE ---
        tvPrioridade.setText(chamadoAtual.getDesc_prioridade());
        int corPrioridade = ContextCompat.getColor(this, R.color.prioridade_default);
        switch (chamadoAtual.getDesc_prioridade().toLowerCase()) {
            case "urgente": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_urgente); break;
            case "alta": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_alta); break;
            case "média": case "media": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_media); break;
            case "baixa": corPrioridade = ContextCompat.getColor(this, R.color.prioridade_baixa); break;
        }
        aplicarCorTag(tvPrioridade, corPrioridade);

        // --- SLA ---
        btnSla.setText("SLA: " + (chamadoAtual.getSla_horas() > 0 ? chamadoAtual.getSla_horas() + "h" : "--"));
        aplicarCorTag(btnSla, ContextCompat.getColor(this, R.color.prioridade_default));

        // --- TEMPO ---
        calcularTempoECorSla();

        // --- TIMELINE ---
        List<InteracaoDTO> listaCompleta = new ArrayList<>();

        Log.d(TAG, "ID CRIADOR (DTO): " + chamadoAtual.getId_usuario());
        Log.d(TAG, "ID LOGADO (PREFS): " + idUsuarioLogado);

        InteracaoDTO descricaoInicial = new InteracaoDTO(
                0, idChamado,
                chamadoAtual.getId_usuario(),
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

    private void aplicarCorTag(TextView view, int cor) {
        view.setTextColor(Color.WHITE);
        GradientDrawable background = (GradientDrawable) view.getBackground().mutate();
        background.setColor(cor);
    }

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
                dataReferencia = new Date();
            }

            long diffMillis = dataReferencia.getTime() - dataAbertura.getTime();
            long horasCorridas = TimeUnit.MILLISECONDS.toHours(diffMillis);
            long dias = TimeUnit.MILLISECONDS.toDays(diffMillis);
            long horas = horasCorridas % 24;
            long minutos = TimeUnit.MILLISECONDS.toMinutes(diffMillis) % 60;

            tvTempo.setText(String.format(Locale.getDefault(), "%dd %02dh %02dm", dias, horas, minutos));

            int slaLimiteHoras = chamadoAtual.getSla_horas();
            int corTempo;

            if (isConcluido) {
                corTempo = ContextCompat.getColor(this, R.color.prioridade_default);
            } else if (slaLimiteHoras > 0) {
                if (horasCorridas >= slaLimiteHoras) {
                    corTempo = ContextCompat.getColor(this, R.color.prioridade_urgente);
                } else if (horasCorridas >= (slaLimiteHoras / 2.0)) {
                    corTempo = ContextCompat.getColor(this, R.color.prioridade_media);
                } else {
                    corTempo = ContextCompat.getColor(this, R.color.prioridade_baixa);
                }
            } else {
                corTempo = ContextCompat.getColor(this, R.color.prioridade_default);
            }
            aplicarCorTag(tvTempo, corTempo);

        } catch (Exception e) {
            tvTempo.setText("--");
            aplicarCorTag(tvTempo, ContextCompat.getColor(this, R.color.prioridade_default));
        }
    }

    private void configurarVisibilidadeBotoes() {
        boolean souCriador = (chamadoAtual.getId_usuario() == idUsuarioLogado);
        Integer idAgenteAtribuido = chamadoAtual.getId_usuario_atribuido();
        boolean souAgenteAtribuido = (idAgenteAtribuido != null && idAgenteAtribuido == idUsuarioLogado);
        boolean estaSemDono = (idAgenteAtribuido == null || idAgenteAtribuido == 0);
        String status = chamadoAtual.getDesc_status().toLowerCase();
        boolean isEncerrado = status.contains("concluído") || status.contains("cancelado");

        layoutResponder.setVisibility(View.GONE);
        btnConcluir.setVisibility(View.GONE);
        btnCancelar.setVisibility(View.GONE);
        btnAssumir.setVisibility(View.GONE);
        btnAtribuir.setVisibility(View.GONE);
        btnEncerrar.setVisibility(View.GONE);
        // ✨ Adicionado btnEncerrar ao reset
        btnEncerrar.setVisibility(View.GONE);

        if (isEncerrado) return;

        boolean podeInteragir = souCriador || souAgenteAtribuido;
        if (idTipoAcessoLogado == 3) podeInteragir = true;

        if (podeInteragir) {
            layoutResponder.setVisibility(View.VISIBLE);
            btnConcluir.setVisibility(View.VISIBLE);
            btnCancelar.setVisibility(View.VISIBLE);
            // ✨ Adicionado btnEncerrar à visibilidade do grupo de interação
            btnEncerrar.setVisibility(View.VISIBLE);
        }

        if (!souCriador) {
            if (idTipoAcessoLogado == 2) { // Agente
                if (estaSemDono) {
                    btnAssumir.setVisibility(View.VISIBLE);
                }
            }
            else if (idTipoAcessoLogado == 3) { // ADM
                btnAtribuir.setVisibility(View.VISIBLE);
            }
        }
    }

    private Uri uriAnexoChat; // Variável para guardar o anexo do chat (preencher no onActivityResult)

    private void enviarResposta() {
        String texto = editResposta.getText().toString().trim();

        // Validação: Precisa ter texto OU arquivo
        if (texto.isEmpty() && uriAnexoChat == null) {
            Toast.makeText(this, "Escreva algo ou anexe um arquivo.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEnviarResposta.setEnabled(false);

        // Passo 1: Enviar Texto (Se houver)
        if (!texto.isEmpty()) {
            apiService.enviarComentario(idChamado, idUsuarioLogado, texto).enqueue(new Callback<AtribuicaoResponse>() {
                @Override
                public void onResponse(Call<AtribuicaoResponse> call, Response<AtribuicaoResponse> response) {
                    // Se tiver anexo, envia agora. Se não, atualiza a tela.
                    if (uriAnexoChat != null) {
                        enviarAnexoChat();
                    } else {
                        limparEAtualizarChat();
                    }
                }
                @Override public void onFailure(Call<AtribuicaoResponse> call, Throwable t) { btnEnviarResposta.setEnabled(true); }
            });
        } else {
            // Se só tem anexo, envia direto
            enviarAnexoChat();
        }
    }

    private void enviarAnexoChat() {
        // Reutilize a mesma lógica do uploadArquivo() acima, mas chamando:
        // apiService.uploadArquivo(...)
        // No onResponse: limparEAtualizarChat();

        // IMPORTANTE: Para aparecer no chat, precisamos também criar uma "Interacao" dizendo que enviou arquivo.
        // Então, dentro do sucesso do upload, chame enviarComentario(..., "[Arquivo Enviado]")
    }

    private void limparEAtualizarChat() {
        editResposta.setText("");
        uriAnexoChat = null;
        btnAnexarChat.setText("Anexar arquivo"); // Reseta texto
        btnEnviarResposta.setEnabled(true);
        buscarDetalhesChamado(); // Recarrega a lista para mostrar a msg nova
    }

    private void assumirChamado() {
        if (chamadoAtual == null || idUsuarioLogado <= 0) return;
        btnAssumir.setEnabled(false);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<AtribuicaoResponse> call = apiService.assumirChamado(chamadoAtual.getId_chamado(), idUsuarioLogado);

        call.enqueue(new Callback<AtribuicaoResponse>() {
            @Override
            public void onResponse(Call<AtribuicaoResponse> call, Response<AtribuicaoResponse> response) {
                btnAssumir.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(DetalheChamadoActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    buscarDetalhesChamado();
                } else {
                    Toast.makeText(DetalheChamadoActivity.this, "Falha ao assumir chamado.", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<AtribuicaoResponse> call, Throwable t) {
                btnAssumir.setEnabled(true);
                Toast.makeText(DetalheChamadoActivity.this, "Erro de rede na atribuição.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- AÇÕES DOS BOTÕES ---

    private void concluirChamado() {
        // ID 4 = Concluído
        enviarAtualizacaoDeStatus(4, "Concluindo chamado...");
    }

    private void cancelarChamado() {
        // ID 5 = Cancelado
        enviarAtualizacaoDeStatus(5, "Cancelando chamado...");
    }

    // Método auxiliar para não repetir código
    private void enviarAtualizacaoDeStatus(int novoStatus, String loadingMsg) {
        if (chamadoAtual == null) return;

        // Opcional: Mostrar um ProgressDialog ou desabilitar botões
        Toast.makeText(this, loadingMsg, Toast.LENGTH_SHORT).show();

        Call<AtribuicaoResponse> call = apiService.atualizarStatus(chamadoAtual.getId_chamado(), novoStatus);

        call.enqueue(new Callback<AtribuicaoResponse>() {
            @Override
            public void onResponse(Call<AtribuicaoResponse> call, Response<AtribuicaoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(DetalheChamadoActivity.this, "Sucesso!", Toast.LENGTH_SHORT).show();
                    // Recarrega a tela para atualizar o visual (status cinza, botões somem)
                    buscarDetalhesChamado();
                } else {
                    Toast.makeText(DetalheChamadoActivity.this, "Falha ao atualizar status.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AtribuicaoResponse> call, Throwable t) {
                Toast.makeText(DetalheChamadoActivity.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}