package com.example.solveit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AberturaChamadoActivity extends AppCompatActivity {

    // --- Variáveis para os componentes do layout ---
    private EditText editTitulo;
    private EditText editNomeSolicitante;
    private Spinner spinnerPrioridade;
    private EditText editEmail;
    private EditText editDescricao;

    // ================================================================
    // ✨ VARIÁVEIS DO CAMPO DE ANEXO ATUALIZADAS PARA O NOVO LAYOUT ✨
    // ================================================================
    private ImageButton btnAnexarIcone;
    private TextView textNomeArquivo;
    // O TIPO DA VARIÁVEL FOI CORRIGIDO AQUI!
    private TextView btnAnexarEscolha; // <-- AJUSTE FEITO: MUDOU DE 'Button' PARA 'TextView'

    // --- Variáveis para os Botões ---
    private Button btnConfirmar;
    private Button btnCancelar;
    private ImageButton btnVoltar;
    private ImageButton btnNotifications;
    private ImageButton btnProfile;

    // --- Variáveis de Controle ---
    private static final int PICK_FILE_REQUEST_CODE = 101;
    private final String PLACEHOLDER = "Selecione";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura_chamado);

        // --- Conectar os componentes (findViewById) ---
        editTitulo = findViewById(R.id.edit_titulo);
        editNomeSolicitante = findViewById(R.id.edit_nome_solicitante);
        spinnerPrioridade = findViewById(R.id.spinner_prioridade);
        editEmail = findViewById(R.id.edit_email);
        editDescricao = findViewById(R.id.edit_descricao);
        btnConfirmar = findViewById(R.id.btn_confirmar);
        btnCancelar = findViewById(R.id.btn_cancelar);
        btnVoltar = findViewById(R.id.btn_voltar);
        btnNotifications = findViewById(R.id.btn_notifications);
        btnProfile = findViewById(R.id.btn_profile);

        // ================================================================
        // ✨ CONEXÃO DOS NOVOS COMPONENTES DE ANEXO ✨
        // ================================================================
        btnAnexarIcone = findViewById(R.id.btn_anexar_icone);
        textNomeArquivo = findViewById(R.id.text_nome_arquivo);
        btnAnexarEscolha = findViewById(R.id.btn_anexar_escolha); // Esta linha agora funciona sem erro


        // --- Configuração dos Componentes ---
        configurarSpinnerPrioridade();

        // --- Configuração dos Listeners (Ações de Clique) ---
        btnVoltar.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> validarEEnviarChamado());
        btnNotifications.setOnClickListener(v -> Toast.makeText(this, "Notificações clicado!", Toast.LENGTH_SHORT).show());
        btnProfile.setOnClickListener(v -> Toast.makeText(this, "Perfil clicado!", Toast.LENGTH_SHORT).show());

        // ================================================================
        // ✨ LISTENERS DOS NOVOS BOTÕES DE ANEXO ✨
        // ================================================================
        btnAnexarIcone.setOnClickListener(v -> abrirSeletorArquivo());
        btnAnexarEscolha.setOnClickListener(v -> abrirSeletorArquivo());
    }

    /**
     * Inicia a intenção para abrir o seletor de arquivos do sistema.
     */
    private void abrirSeletorArquivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Selecione o arquivo"), PICK_FILE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Nenhum gerenciador de arquivos encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Trata o retorno do seletor de arquivos, obtendo o nome do arquivo e exibindo-o.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String nomeArquivo = "Arquivo selecionado"; // Valor padrão
            if (uri != null) {
                // Tenta obter o nome do arquivo de forma segura
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            nomeArquivo = cursor.getString(nameIndex);
                        }
                    }
                }
            }
            // ================================================================
            // ✨ USANDO O NOVO TEXTVIEW PARA MOSTRAR O NOME DO ARQUIVO ✨
            // ================================================================
            textNomeArquivo.setText(nomeArquivo);
            textNomeArquivo.setTextColor(Color.BLACK); // Deixa o texto preto para indicar seleção
            Toast.makeText(this, "Arquivo: " + nomeArquivo, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Valida os campos obrigatórios e, se tudo estiver correto, simula o envio.
     */
    private void validarEEnviarChamado() {
        String titulo = editTitulo.getText().toString().trim();
        String nomeSolicitante = editNomeSolicitante.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();
        String prioridade = spinnerPrioridade.getSelectedItem().toString();

        boolean houveErro = false;

        if (titulo.isEmpty()) { editTitulo.setError("Obrigatório."); houveErro = true; }
        if (nomeSolicitante.isEmpty()) { editNomeSolicitante.setError("Obrigatório."); houveErro = true; }
        if (email.isEmpty()) { editEmail.setError("Obrigatório."); houveErro = true; }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { editEmail.setError("E-mail inválido."); houveErro = true; }
        if (descricao.isEmpty()) { editDescricao.setError("Obrigatório."); houveErro = true; }
        if (prioridade.equals(PLACEHOLDER)) {
            Toast.makeText(this, "Selecione a Prioridade.", Toast.LENGTH_SHORT).show();
            houveErro = true;
        }

        if (!houveErro) {
            Toast.makeText(this, "CHAMADO ENVIADO COM SUCESSO!", Toast.LENGTH_LONG).show();
            finish(); // Fecha a tela após o sucesso
        }
    }

    /**
     * Configura o Spinner de Prioridade, incluindo a lógica de cor para o placeholder.
     */
    private void configurarSpinnerPrioridade() {
        String[] prioridades = new String[]{PLACEHOLDER, "Baixa", "Média", "Alta"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, prioridades) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(adapter);
    }
}
