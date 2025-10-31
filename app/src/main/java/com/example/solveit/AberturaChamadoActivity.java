package com.example.solveit;

// Imports (Verifique se todos necessários estão aqui)
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences; // Import para SharedPreferences
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

// Imports da API, DTOs e Retrofit
import com.example.solveit.api.AbrirChamadoResponse;
import com.example.solveit.api.ApiService;
import com.example.solveit.api.CategoriaDTO;
import com.example.solveit.api.RetrofitClient;

import java.io.IOException; // Para tratar erro do errorBody
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AberturaChamadoActivity extends AppCompatActivity {

    private static final String TAG = "AberturaChamado";

    // --- Variáveis da UI ---
    private EditText editTitulo;
    private EditText editNomeSolicitante; // Mantido para exibir o nome
    private Spinner spinnerPrioridade;
    private Spinner spinnerCategoria;
    private EditText editEmail;
    private EditText editDescricao;
    private ImageButton btnAnexarIcone;
    private TextView textNomeArquivo;
    private TextView btnAnexarEscolha;
    private Button btnConfirmar;
    private Button btnCancelar;
    private ImageButton btnVoltar;

    // --- Variáveis de Controle ---
    private ApiService apiService;
    private List<CategoriaDTO> listaCategorias = new ArrayList<>();
    private int selectedCategoriaId = -1;
    private int idUsuarioLogado = -1; // Guarda o ID do usuário logado

    private static final int PICK_FILE_REQUEST_CODE = 101;
    private final String PLACEHOLDER = "Selecione";
    private final String CATEGORIA_PLACEHOLDER_TEXT = "Selecione a Categoria";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura_chamado);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // --- findViewById ---
        editTitulo = findViewById(R.id.edit_titulo);
        editNomeSolicitante = findViewById(R.id.edit_nome_solicitante);
        spinnerPrioridade = findViewById(R.id.spinner_prioridade);
        spinnerCategoria = findViewById(R.id.spinner_categoria);
        editEmail = findViewById(R.id.edit_email);
        editDescricao = findViewById(R.id.edit_descricao);
        btnConfirmar = findViewById(R.id.btn_confirmar);
        btnCancelar = findViewById(R.id.btn_cancelar);
        btnVoltar = findViewById(R.id.btn_voltar);
        btnAnexarIcone = findViewById(R.id.btn_anexar_icone);
        textNomeArquivo = findViewById(R.id.text_nome_arquivo);
        btnAnexarEscolha = findViewById(R.id.btn_anexar_escolha);

        // --- Carrega dados do usuário logado e preenche o nome ---
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String nomeUsuarioLogado = prefs.getString(MainActivity.KEY_USER_NAME, "Usuário Desconhecido");
        idUsuarioLogado = prefs.getInt(MainActivity.KEY_USER_ID, -1);

        Log.d(TAG, "Dados lidos do SharedPreferences: ID=" + idUsuarioLogado + ", Nome=" + nomeUsuarioLogado);

        if (idUsuarioLogado <= 0) {
            Log.e(TAG, "ERRO CRÍTICO: ID do usuário logado não encontrado ou inválido!");
            Toast.makeText(this, "Erro: Não foi possível identificar o usuário logado.", Toast.LENGTH_LONG).show();
            finish(); return;
        }

        editNomeSolicitante.setText(nomeUsuarioLogado);
        editNomeSolicitante.setEnabled(false);
        editNomeSolicitante.setFocusable(false);
        editNomeSolicitante.setClickable(false);
        // --- Fim do preenchimento do nome ---

        // --- Configurações Iniciais ---
        configurarSpinnerPrioridade();
        configurarListeners();
        buscarCategorias();
    }

    // --- Configura os Listeners de Clique ---
    private void configurarListeners() {
        btnVoltar.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> validarEEnviarChamado());
        findViewById(R.id.ic_notifications).setOnClickListener(v -> Toast.makeText(this, "Notificações!", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btn_profile).setOnClickListener(v -> Toast.makeText(this, "Perfil!", Toast.LENGTH_SHORT).show());
        btnAnexarIcone.setOnClickListener(v -> abrirSeletorArquivo());
        btnAnexarEscolha.setOnClickListener(v -> abrirSeletorArquivo());
    }

    // --- Busca as Categorias da API ---
    private void buscarCategorias() {
        Log.d(TAG, "Buscando categorias da API...");
        Call<List<CategoriaDTO>> call = apiService.getCategorias();
        call.enqueue(new Callback<List<CategoriaDTO>>() {
            @Override
            public void onResponse(Call<List<CategoriaDTO>> call, Response<List<CategoriaDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCategorias = new ArrayList<>(response.body());
                    CategoriaDTO placeholder = new CategoriaDTO() { @Override public String toString() { return CATEGORIA_PLACEHOLDER_TEXT; } };
                    listaCategorias.add(0, placeholder);
                    configurarSpinnerCategoria();
                } else { Log.e(TAG, "Erro ao buscar categorias: Código " + response.code()); Toast.makeText(AberturaChamadoActivity.this, "Erro ao carregar categorias.", Toast.LENGTH_SHORT).show(); configurarSpinnerCategoriaErro(); }
            }
            @Override
            public void onFailure(Call<List<CategoriaDTO>> call, Throwable t) { Log.e(TAG, "Falha na rede ao buscar categorias: ", t); Toast.makeText(AberturaChamadoActivity.this, "Falha de rede ao carregar categorias.", Toast.LENGTH_SHORT).show(); configurarSpinnerCategoriaErro(); }
        });
    }

    // --- Configura o Spinner de Categoria ---
    private void configurarSpinnerCategoria() {
        ArrayAdapter<CategoriaDTO> adapter = new ArrayAdapter<CategoriaDTO>( this, android.R.layout.simple_spinner_item, listaCategorias ){
            @Override public boolean isEnabled(int position){ return position != 0; }
            @Override public View getDropDownView(int pos, View cv, @NonNull ViewGroup p) { View v = super.getDropDownView(pos, cv, p); TextView tv = (TextView) v; tv.setTextColor(pos == 0 ? Color.GRAY : Color.BLACK); return v; }
            @Override public View getView(int pos, View cv, ViewGroup p) { View v = super.getView(pos, cv, p); TextView tv = (TextView) v; tv.setTextColor(pos == 0 ? Color.GRAY : Color.BLACK); return v; }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoriaId = (position == 0) ? -1 : ((CategoriaDTO) parent.getItemAtPosition(position)).getId_categoria();
                Log.d(TAG, "Categoria selecionada: ID = " + selectedCategoriaId);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { selectedCategoriaId = -1; }
        });
        spinnerCategoria.setSelection(0);
    }

    // --- Configura spinner em caso de erro ---
    private void configurarSpinnerCategoriaErro() {
        List<String> erroList = new ArrayList<>(); erroList.add("Erro ao carregar");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, erroList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter); spinnerCategoria.setEnabled(false);
    }

    // --- Configura o Spinner de Prioridade ---
    private void configurarSpinnerPrioridade() {
        final String[] prioridades = new String[]{PLACEHOLDER, "Urgente", "Alta", "Média", "Baixa"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, prioridades){
            @Override public boolean isEnabled(int position){ return position != 0; }
            @Override public View getDropDownView(int pos, View cv, @NonNull ViewGroup p) { View v = super.getDropDownView(pos, cv, p); TextView tv = (TextView) v; tv.setTextColor(pos == 0 ? Color.GRAY : Color.BLACK); return v; }
            @Override public View getView(int pos, View cv, ViewGroup p) { View v = super.getView(pos, cv, p); TextView tv = (TextView) v; tv.setTextColor(pos == 0 ? Color.GRAY : Color.BLACK); return v; }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(adapter);
        spinnerPrioridade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinnerPrioridade.setSelection(0);
    }

    // --- Valida e Envia o Chamado para a API ---
    private void validarEEnviarChamado() {
        // Coleta de dados
        String titulo = editTitulo.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();
        String prioridade = spinnerPrioridade.getSelectedItem().toString();
        int idCategoria = selectedCategoriaId;
        int idUsuarioAbertura = idUsuarioLogado; // Usa o ID guardado no onCreate

        // Validações
        boolean houveErro = false;
        if (titulo.isEmpty()) { editTitulo.setError("Obrigatório."); houveErro = true; }
        if (email.isEmpty()) { editEmail.setError("Obrigatório."); houveErro = true; }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { editEmail.setError("E-mail inválido."); houveErro = true; }
        if (descricao.isEmpty()) { editDescricao.setError("Obrigatório."); houveErro = true; }
        if (prioridade.equals(PLACEHOLDER)) { Toast.makeText(this, "Selecione a Prioridade.", Toast.LENGTH_SHORT).show(); houveErro = true; }
        if (idCategoria <= 0) { Toast.makeText(this, "Selecione a Categoria.", Toast.LENGTH_SHORT).show(); houveErro = true; }
        if (idUsuarioAbertura <= 0) { Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_LONG).show(); houveErro = true; } // Valida o ID

        // Se passou nas validações, envia para a API
        if (!houveErro) {
            // ✨ LOG DE VERIFICAÇÃO ADICIONADO AQUI ✨
            Log.d(TAG, "===> Preparando para enviar API. Valores:");
            Log.d(TAG, "     titulo: " + titulo);
            Log.d(TAG, "     idUsuarioAbertura: " + idUsuarioAbertura); // <<< O VALOR ESTÁ CORRETO AQUI?
            Log.d(TAG, "     prioridade: " + prioridade);
            Log.d(TAG, "     idCategoria: " + idCategoria);
            Log.d(TAG, "     email: " + email);
            Log.d(TAG, "     descricao: " + descricao);
            Log.d(TAG, "========================================");

            btnConfirmar.setEnabled(false);

            // Chama a API
            Call<AbrirChamadoResponse> call = apiService.abrirChamado(
                    titulo,
                    idUsuarioAbertura, // ✨ Garanta que esta variável tem o valor correto ✨
                    prioridade,
                    idCategoria,
                    email,
                    descricao
            );

            call.enqueue(new Callback<AbrirChamadoResponse>() {
                @Override
                public void onResponse(Call<AbrirChamadoResponse> call, Response<AbrirChamadoResponse> response) {
                    btnConfirmar.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        AbrirChamadoResponse apiResponse = response.body();
                        Log.d(TAG, "Chamado aberto! ID: " + (apiResponse.getId_chamado() != null ? apiResponse.getId_chamado() : "N/A"));
                        Toast.makeText(AberturaChamadoActivity.this, "Chamado aberto com sucesso!", Toast.LENGTH_LONG).show();
                        // Aqui você pode iniciar o upload do anexo usando apiResponse.getId_chamado()
                        finish();
                    } else {
                        String errorMsg = "Erro ao abrir chamado.";
                        if (response.body() != null && response.body().getMessage() != null) { errorMsg = response.body().getMessage(); }
                        else if (response.errorBody() != null) { try { errorMsg += " (" + response.errorBody().string() + ")"; } catch (IOException e) {Log.e(TAG, "Erro lendo errorBody", e);} }
                        else { errorMsg += " (Código: " + response.code() + ")"; }
                        Log.e(TAG, "Erro da API ao abrir chamado: " + errorMsg);
                        Toast.makeText(AberturaChamadoActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<AbrirChamadoResponse> call, Throwable t) {
                    btnConfirmar.setEnabled(true);
                    Log.e(TAG, "Falha na rede ao abrir chamado: ", t);
                    Toast.makeText(AberturaChamadoActivity.this, "Falha de rede. Não foi possível abrir o chamado.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // --- Métodos para Anexar Arquivo ---
    private void abrirSeletorArquivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); intent.setType("*/*"); intent.addCategory(Intent.CATEGORY_OPENABLE);
        try { startActivityForResult(Intent.createChooser(intent, "Selecione o arquivo"), PICK_FILE_REQUEST_CODE); }
        catch (android.content.ActivityNotFoundException ex) { Toast.makeText(this, "Nenhum gerenciador de arquivos encontrado.", Toast.LENGTH_SHORT).show(); }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // ✨ ADICIONADO: Chamada ao super que estava faltando ✨
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData(); String nomeArquivo = "Arquivo selecionado";
            if (uri != null) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) { nomeArquivo = cursor.getString(nameIndex); }
                    }
                } catch (Exception e) { Log.e(TAG, "Erro ao obter nome do arquivo", e); }
            }
            textNomeArquivo.setText(nomeArquivo); textNomeArquivo.setTextColor(Color.BLACK);
            Toast.makeText(this, "Arquivo selecionado: " + nomeArquivo, Toast.LENGTH_SHORT).show();
            // Guarde a Uri aqui (ex: private Uri selectedFileUri = null;)
            // selectedFileUri = uri;
        }
    }
}