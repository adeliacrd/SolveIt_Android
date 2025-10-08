package com.example.solveit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View; // NOVO: Necessário para a lógica do Spinner
import android.widget.AdapterView; // NOVO: Necessário para o Listener do Spinner
import android.graphics.Color; // NOVO: Necessário para definir a cor cinza

public class AberturaChamadoActivity extends AppCompatActivity {

    // 1. Variáveis para os campos de entrada
    private EditText editTitulo;
    private EditText editNomeSolicitante;
    private Spinner spinnerPrioridade;
    private EditText editEmail;
    private EditText editDescricao;
    private TextView editArquivoSelecionado;

    // Variáveis do Spinner para controle de cor (replicando a lógica do Registro)
    private int defaultSpinnerTextColor;
    private final String PLACEHOLDER = "Selecione";

    // 2. Variáveis para os Botões
    private Button btnConfirmar;
    private Button btnCancelar;
    private Button btnEscolherArquivo;
    private ImageButton btnAnexarSimbolo;
    private ImageButton btnVoltar;

    // Código de solicitação do arquivo
    private static final int PICK_FILE_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura_chamado);

        // 3. Ligar os componentes (findViewById)
        // Campos de Formulário
        editTitulo = findViewById(R.id.edit_titulo);
        editNomeSolicitante = findViewById(R.id.edit_nome_solicitante);
        editEmail = findViewById(R.id.edit_email);
        editDescricao = findViewById(R.id.edit_descricao);
        editArquivoSelecionado = findViewById(R.id.edit_arquivo_selecionado);

        // Spinner
        spinnerPrioridade = findViewById(R.id.spinner_prioridade);
        configurarSpinnerPrioridade(); // Chamada para o novo método corrigido

        // Botões de Ação
        btnConfirmar = findViewById(R.id.btn_confirmar);
        btnCancelar = findViewById(R.id.btn_cancelar);
        btnEscolherArquivo = findViewById(R.id.btn_escolher_arquivo);
        btnAnexarSimbolo = findViewById(R.id.btn_anexar_simbolo);

        // Botão de Voltar da Toolbar
        btnVoltar = findViewById(R.id.btn_voltar);


        // ************ 4. LÓGICA DO BOTÃO CONFIRMAR (VALIDAÇÃO) ************
        btnConfirmar.setOnClickListener(view -> {
            validarEEnviarChamado();
        });

        // ************ 5. LÓGICA DO BOTÃO CANCELAR ************
        btnCancelar.setOnClickListener(view -> {
            finish(); // Fecha a tela atual
        });

        // ************ 6. LÓGICA DO BOTÃO VOLTAR (TOOLBAR) ************
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> {
                finish();
            });
        }


        // ************ 7. LÓGICA DO ANEXO DE ARQUIVO ************
        btnEscolherArquivo.setOnClickListener(view -> {
            abrirSeletorArquivo();
        });

        btnAnexarSimbolo.setOnClickListener(view -> {
            abrirSeletorArquivo();
        });
    }

    // *******************************************************************
    // MÉTODOS DE FUNCIONALIDADE (VALIDAÇÃO, SPINNER, ARQUIVO)
    // *******************************************************************

    /**
     * MÉTODO CORRIGIDO: Configuração do Spinner de Prioridade com lógica de cor dinâmica.
     */
    private void configurarSpinnerPrioridade() {
        // Opções do Spinner
        String[] prioridades = new String[] {
                PLACEHOLDER,
                "Baixa",
                "Média",
                "Alta"
        };

        // Usa o layout customizado (custom_spinner_selected_item) para o tamanho da fonte.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_selected_item,
                android.R.id.text1,
                prioridades
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(adapter);

        // 1. Capturar a cor padrão do texto (para voltar a cor se o usuário selecionar uma opção válida)
        // Isso garante que a cor do texto nas opções válidas não seja cinza.
        if (spinnerPrioridade.getCount() > 0) {
            TextView tempTextView = new TextView(this);
            tempTextView.setTextAppearance(android.R.style.TextAppearance_Widget_TextView_SpinnerItem);
            defaultSpinnerTextColor = tempTextView.getCurrentTextColor();
            if (defaultSpinnerTextColor == 0) defaultSpinnerTextColor = Color.BLACK; // Fallback para preto
        }

        // 2. Aplicar a lógica de cor cinza para o placeholder ("Selecione")
        spinnerPrioridade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedTextView = (view instanceof TextView) ? (TextView) view : null;

                if (position == 0) {
                    // Opção "Selecione" selecionada (Placeholder)
                    if (selectedTextView != null) selectedTextView.setTextColor(Color.GRAY);
                } else {
                    // Opção válida selecionada
                    if (selectedTextView != null) selectedTextView.setTextColor(defaultSpinnerTextColor);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não é estritamente necessário, pois onItemSelected é chamado no início, mas é boa prática
                View currentSelectedView = spinnerPrioridade.getSelectedView();
                if (currentSelectedView instanceof TextView) {
                    ((TextView) currentSelectedView).setTextColor(Color.GRAY);
                }
            }
        });

        // 3. Configurar o estado inicial como cinza (usando post para garantir o desenho)
        spinnerPrioridade.setSelection(0);
        spinnerPrioridade.post(new Runnable() {
            @Override
            public void run() {
                View selectedView = spinnerPrioridade.getSelectedView();
                if (selectedView instanceof TextView) {
                    ((TextView) selectedView).setTextColor(Color.GRAY);
                }
            }
        });
    }

    /**
     * Valida os 5 campos obrigatórios e, se tudo OK, simula o envio.
     */
    private void validarEEnviarChamado() {
        // 1. Coletar todos os dados dos campos
        String titulo = editTitulo.getText().toString().trim();
        String nomeSolicitante = editNomeSolicitante.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();

        // Capturar Prioridade do Spinner
        String prioridade = spinnerPrioridade.getSelectedItem() != null ?
                spinnerPrioridade.getSelectedItem().toString() : "";

        boolean houveErro = false;

        // --- VALIDAÇÃO ---
        if (titulo.isEmpty()) { editTitulo.setError("Obrigatório."); editTitulo.requestFocus(); houveErro = true; } else { editTitulo.setError(null); }
        if (nomeSolicitante.isEmpty()) { editNomeSolicitante.setError("Obrigatório."); editNomeSolicitante.requestFocus(); houveErro = true; } else { editNomeSolicitante.setError(null); }
        if (email.isEmpty()) { editEmail.setError("Obrigatório."); editEmail.requestFocus(); houveErro = true; }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { editEmail.setError("Formato de e-mail inválido."); editEmail.requestFocus(); houveErro = true; }
        else { editEmail.setError(null); }
        if (descricao.isEmpty()) { editDescricao.setError("Obrigatório."); editDescricao.requestFocus(); houveErro = true; } else { editDescricao.setError(null); }

        if (prioridade.equals(PLACEHOLDER) || prioridade.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione a Prioridade (*)", Toast.LENGTH_LONG).show();
            houveErro = true;
        }

        // 3. AÇÃO FINAL: Se não houve erro, simular o envio
        if (!houveErro) {
            String nomeArquivo = editArquivoSelecionado.getText().toString().equals("Selecione o arquivo") ?
                    "Nenhum" : editArquivoSelecionado.getText().toString();

            String dadosFinais = "SUCESSO! Chamado pronto para envio. Título: " + titulo +
                    " | Prioridade: " + prioridade + " | Arquivo: " + nomeArquivo;

            Toast.makeText(this, "CHAMADO ENVIADO! " + dadosFinais, Toast.LENGTH_LONG).show();
        }
    }


    // MÉTODO: Abre o seletor de arquivos do sistema Android
    private void abrirSeletorArquivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "Selecione o arquivo para o chamado"), PICK_FILE_REQUEST_CODE);
    }

    // MÉTODO: Recebe o resultado da seleção de arquivo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                android.net.Uri uri = data.getData();
                String path = uri.getPath();

                // Trata o path para obter o nome do arquivo
                String nomeArquivo = path.substring(path.lastIndexOf('/') + 1);

                // Exibe o nome do arquivo no campo de texto
                editArquivoSelecionado.setText(nomeArquivo);

                Toast.makeText(this, "Arquivo selecionado: " + nomeArquivo, Toast.LENGTH_LONG).show();
            }
        }
    }
}