package com.example.solveit;

// Imports básicos
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View; // << CORREÇÃO: Usar android.view.View
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
    private RecyclerView rvMensagens;

    // Lançador para o seletor de arquivos
    private ActivityResultLauncher<Intent> seletorDeArquivosLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa_chamado);

        // Inicializa o lançador do seletor de arquivos ANTES de ser usado
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
                        // CORREÇÃO: LENGTH_SHORT em vez de LENGT_SHORT
                        Toast.makeText(this, "Nenhum arquivo selecionado.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Chama os métodos de configuração
        conectarComponentes();
        configurarToolbar();
        preencherComDadosDeExemplo();
        configurarListaDeMensagens();
        configurarLinkAnexo();
    }

    // =====================================================================================
    //  ✨ CORREÇÃO: Todos os métodos que estavam faltando foram adicionados de volta abaixo ✨
    // =====================================================================================

    private void conectarComponentes() {
        tvChamadoId = findViewById(R.id.tv_conversa_id);
        tvStatus = findViewById(R.id.tv_conversa_status);
        tvPrioridade = findViewById(R.id.tv_conversa_prioridade);
        tvTituloValor = findViewById(R.id.tv_conversa_titulo_valor);
        tvSolicitante = findViewById(R.id.tv_conversa_solicitante);
        tvAgente = findViewById(R.id.tv_conversa_agente);
        rvMensagens = findViewById(R.id.rv_mensagens);
        tvAnexarArquivo = findViewById(R.id.tv_anexar_arquivo);
    }

    private void configurarToolbar() {
        // Encontra a Toolbar no layout
        Toolbar toolbar = findViewById(R.id.toolbar_padrao);

        // ✨ PASSO 1: Define o título diretamente no componente Toolbar.
        toolbar.setTitle("Informações");

        // ✨ PASSO 2: Agora sim, promove a Toolbar (já com o título) para ser a ActionBar oficial.
        setSupportActionBar(toolbar);

        // ✨ PASSO 3: Adiciona o botão de "voltar" (seta) na ActionBar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ✨ PASSO 4: Define a ação de clique para o botão de voltar.
        toolbar.setNavigationOnClickListener(v -> finish());
    }



    private void preencherComDadosDeExemplo() {
        tvChamadoId.setText("Chamado (ID 08)");
        tvTituloValor.setText("Impressora não funciona na sala de reuniões");
        tvStatus.setText("Em Atendimento");
        tvPrioridade.setText("Urgente");
        tvSolicitante.setText("Nome do Usuário\nnome.usuario@empresa.com");
        tvAgente.setText("Agente: Nome do Agente");
        tvAgente.setVisibility(View.VISIBLE);
        configurarTag(tvStatus, "Em Atendimento");
        configurarTag(tvPrioridade, "Urgente");
    }

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
            public void onClick(View widget) { // << CORREÇÃO: Usar android.view.View
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
