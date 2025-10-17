package com.example.solveit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity; // ✨ IMPORT NECESSÁRIO PARA O ALINHAMENTO
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class AberturaChamadoActivity extends AppCompatActivity {

    // (Suas variáveis de classe permanecem as mesmas)
    private EditText editTitulo;
    private EditText editNomeSolicitante;
    private Spinner spinnerPrioridade;
    private EditText editEmail;
    private EditText editDescricao;
    private ImageButton btnAnexarIcone;
    private TextView textNomeArquivo;
    private TextView btnAnexarEscolha;
    private Button btnConfirmar;
    private Button btnCancelar;
    private ImageButton btnVoltar;
    private ImageButton btnNotifications;
    private ImageButton btnProfile;
    private static final int PICK_FILE_REQUEST_CODE = 101;
    private final String PLACEHOLDER = "Selecione";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura_chamado);

        // (Seu código de findViewById permanece igual)
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
        btnAnexarIcone = findViewById(R.id.btn_anexar_icone);
        textNomeArquivo = findViewById(R.id.text_nome_arquivo);
        btnAnexarEscolha = findViewById(R.id.btn_anexar_escolha);

        configurarSpinnerPrioridade();
        configurarListeners();
    }

    private void configurarListeners() {
        btnVoltar.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> validarEEnviarChamado());
        btnNotifications.setOnClickListener(v -> Toast.makeText(this, "Notificações clicado!", Toast.LENGTH_SHORT).show());
        btnProfile.setOnClickListener(v -> Toast.makeText(this, "Perfil clicado!", Toast.LENGTH_SHORT).show());
        btnAnexarIcone.setOnClickListener(v -> abrirSeletorArquivo());
        btnAnexarEscolha.setOnClickListener(v -> abrirSeletorArquivo());
    }

    private void configurarSpinnerPrioridade() {
        // As listas de dados permanecem as mesmas
        final String[] prioridades = new String[]{PLACEHOLDER, "Urgente", "Alta", "Média", "Baixa"};
        final int[] coresFundo = new int[]{
                R.color.prioridade_urgente, // Esta lista agora tem 4 itens
                R.color.prioridade_alta,
                R.color.prioridade_media,
                R.color.prioridade_baixa
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_prioridade, android.R.id.text1, prioridades) {

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                if (position == 0) {
                    textView.setHeight(0);
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setHeight((int) (48 * getContext().getResources().getDisplayMetrics().density));
                    textView.setVisibility(View.VISIBLE);
                    GradientDrawable background = (GradientDrawable) textView.getBackground().mutate();
                    // O índice das cores é `position - 1` porque a lista de cores não tem o placeholder
                    background.setColor(ContextCompat.getColor(getContext(), coresFundo[position - 1]));
                }
                return view;
            }

            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                // --- ✨ ÚNICA ALTERAÇÃO ESTÁ AQUI: DEFININDO O ALINHAMENTO ✨ ---
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                // Alinha o texto à esquerda (START) e o centraliza verticalmente
                textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                textView.setBackgroundColor(Color.TRANSPARENT);

                return view;
            }
        };

        spinnerPrioridade.setAdapter(adapter);
    }

    // (O resto do seu código, validarEEnviarChamado, onActivityResult, etc., permanece igual)
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
            finish();
        }
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String nomeArquivo = "Arquivo selecionado";
            if (uri != null) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            nomeArquivo = cursor.getString(nameIndex);
                        }
                    }
                }
            }
            textNomeArquivo.setText(nomeArquivo);
            textNomeArquivo.setTextColor(Color.BLACK);
            Toast.makeText(this, "Arquivo: " + nomeArquivo, Toast.LENGTH_SHORT).show();
        }
    }
}
