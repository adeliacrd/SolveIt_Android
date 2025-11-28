package com.example.solveit;

// Imports básicosimport android.content.Intent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

// Imports para o seletor de arquivos
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

// Imports para a lista (RecyclerView)
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ConversaChamadoActivity extends AppCompatActivity {

    // Variáveis para os componentes visuais
    private TextView tvChamadoId, tvStatus, tvPrioridade, tvTituloValor, tvSolicitante, tvAgente, tvAnexarArquivo;
    private Button btnEditarChamado;
    private RecyclerView rvMensagens;

    // Lançador para o seletor de arquivos
    private ActivityResultLauncher<Intent> seletorDeArquivosLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa_chamado);

        seletorDeArquivosLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri arquivoUri = data.getData();
                            Toast.makeText(this, "Arquivo selecionado: " + arquivoUri.getPath(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Nenhum arquivo selecionado.", Toast.LENGTH_SHORT).show();
                    }
                });

        conectarComponentes();
        configurarToolbar();
        preencherComDadosDeExemplo(); // Este método agora conterá a lógica principal
        configurarListaDeMensagens();
        configurarLinkAnexo();
    }

    private void conectarComponentes() {
        tvChamadoId = findViewById(R.id.tv_conversa_id);
        tvStatus = findViewById(R.id.tv_conversa_status);
        tvPrioridade = findViewById(R.id.tv_conversa_prioridade);
        tvTituloValor = findViewById(R.id.tv_conversa_titulo_valor);
        tvSolicitante = findViewById(R.id.tv_conversa_solicitante);
        tvAgente = findViewById(R.id.tv_conversa_agente);
        rvMensagens = findViewById(R.id.rv_mensagens);
        tvAnexarArquivo = findViewById(R.id.tv_anexar_arquivo);
        btnEditarChamado = findViewById(R.id.btn_editar_chamado);
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_padrao);
        toolbar.setTitle("Informações");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void preencherComDadosDeExemplo() {
        // --- Preenchimento dos dados (seu código atual) ---
        tvChamadoId.setText("Chamado (ID 08)");
        tvTituloValor.setText("Impressora não funciona na sala de reuniões");
        tvStatus.setText("Em Atendimento");
        tvPrioridade.setText("Urgente");
        tvSolicitante.setText("Nome do Usuário\nnome.usuario@empresa.com");
        tvAgente.setText("Agente: Nome do Agente");
        tvAgente.setVisibility(View.VISIBLE);
        configurarTag(tvStatus, "Em Atendimento");
        configurarTag(tvPrioridade, "Urgente");

        // ======================================================================
        // LÓGICA DE VISIBILIDADE E AÇÃO DO BOTÃO "EDITAR"
        // ======================================================================

        // 1. Lê as informações salvas no momento do login
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        int idTipoAcesso = prefs.getInt(MainActivity.KEY_USER_ROLE_ID, 0);

        // 2. Verifica se o ID do perfil é de Administrador (ID = 3)
        if (idTipoAcesso == 3) {
            // Se for ADM, torna o botão visível
            btnEditarChamado.setVisibility(View.VISIBLE);

            // =========================================================
            // Define a ação de clique para abrir a tela de edição
            // =========================================================
            btnEditarChamado.setOnClickListener(v -> {
                // 1. ✅ CORREÇÃO: Cria a intenção de abrir a NOVA tela com abas
                Intent intent = new Intent(ConversaChamadoActivity.this, AdminEdicaoActivity.class);

                // 2. Coleta os dados da tela atual para enviar à tela de edição (IMPORTANTE MANTER)
                String tituloAtual = tvTituloValor.getText().toString();
                String solicitanteCompleto = tvSolicitante.getText().toString();
                // Pega apenas a primeira linha do texto do solicitante, que é o nome
                String nomeSolicitante = solicitanteCompleto.split("\n")[0];

                // 3. Adiciona os dados na "bagagem" da Intent (IMPORTANTE MANTER)
                intent.putExtra("CHAMADO_TITULO", tituloAtual);
                intent.putExtra("CHAMADO_SOLICITANTE", nomeSolicitante);
                // Você pode adicionar mais dados que precise na tela de edição
                // intent.putExtra("CHAMADO_ID", idDoChamado);
                // intent.putExtra("CHAMADO_DESCRICAO", descricaoOriginal);

                // 4. Inicia a nova tela, levando os dados com ela
                startActivity(intent);
            });
            // =========================================================

        } else {
            // Se NÃO for ADM, o botão fica completamente escondido
            btnEditarChamado.setVisibility(View.GONE);
        }
    }

    // O resto do seu código permanece exatamente igual
    // ...
    private void configurarTag(TextView textView, String texto) {
        GradientDrawable background = (GradientDrawable) textView.getBackground().mutate();
        int cor;
        switch (texto.toLowerCase()) {
            case "em aberto": case "em atendimento": cor = Color.parseColor("#27AE60"); break;
            case "urgente": case "alta": cor = Color.parseColor("#E74C3C"); break;
            case "concluído": case "cancelado": cor = Color.parseColor("#7F8C8D"); break;
            default: cor = Color.parseColor("#95A5A6"); break;
        }
        background.setColor(cor);
    }

    private void configurarListaDeMensagens() {
        List<Mensagem> listaDeMensagens = new ArrayList<>();
        listaDeMensagens.add(new Mensagem("NC", "NomeCliente", "Descrição", "29/04/25 às 16h32", "A impressora da sala de reuniões simplesmente parou de funcionar. Já tentei reiniciar e nada acontece."));
        listaDeMensagens.add(new Mensagem("NA", "NomeAgente", "Mensagem", "29/04/25 às 18h05", "Olá! Estou a caminho para verificar o problema. Por favor, deixe a sala destrancada."));
        MensagensAdapter adapter = new MensagensAdapter(listaDeMensagens);
        rvMensagens.setLayoutManager(new LinearLayoutManager(this));
        rvMensagens.setAdapter(adapter);
    }

    private void configurarLinkAnexo() {
        String textoCompleto = "Para anexar arquivos, clique aqui";
        String textoClicavel = "clique aqui";
        SpannableString spannableString = new SpannableString(textoCompleto);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                abrirSeletorDeArquivos();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(getResources().getColor(R.color.azul_link));
            }
        };

        int inicio = textoCompleto.indexOf(textoClicavel);
        int fim = inicio + textoClicavel.length();
        spannableString.setSpan(clickableSpan, inicio, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvAnexarArquivo.setText(spannableString);
        tvAnexarArquivo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void abrirSeletorDeArquivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        seletorDeArquivosLauncher.launch(intent);
    }
}
