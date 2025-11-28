package com.example.solveit;

// Imports
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
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

import com.example.solveit.api.AbrirChamadoResponse;
import com.example.solveit.api.AtribuicaoResponse; // ✨ Import necessário para resposta de upload
import com.example.solveit.api.ApiService;
import com.example.solveit.api.CategoriaDTO;
import com.example.solveit.api.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AberturaChamadoActivity extends AppCompatActivity {

    private static final String TAG = "AberturaChamado";

    // --- Variáveis da UI ---
    private EditText editTitulo;
    private EditText editNomeSolicitante;
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
    private int idUsuarioLogado = -1;

    // ✨ VARIÁVEL NOVA PARA GUARDAR A URI DO ARQUIVO ✨
    private Uri uriSelecionada = null;

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

        // --- Carrega dados do usuário ---
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String nomeUsuarioLogado = prefs.getString(MainActivity.KEY_USER_NAME, "Usuário Desconhecido");
        idUsuarioLogado = prefs.getInt(MainActivity.KEY_USER_ID, -1);

        if (idUsuarioLogado <= 0) {
            Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_LONG).show();
            finish(); return;
        }

        editNomeSolicitante.setText(nomeUsuarioLogado);
        editNomeSolicitante.setEnabled(false);
        editNomeSolicitante.setFocusable(false);
        editNomeSolicitante.setClickable(false);

        // --- Configurações Iniciais ---
        configurarSpinnerPrioridade();
        configurarListeners();
        buscarCategorias();
    }

    private void configurarListeners() {
        btnVoltar.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> validarEEnviarChamado());
        // ... (outros listeners de toolbar omitidos para brevidade) ...
        btnAnexarIcone.setOnClickListener(v -> abrirSeletorArquivo());
        btnAnexarEscolha.setOnClickListener(v -> abrirSeletorArquivo());
    }

    private void buscarCategorias() {
        Call<List<CategoriaDTO>> call = apiService.getCategorias();
        call.enqueue(new Callback<List<CategoriaDTO>>() {
            @Override
            public void onResponse(Call<List<CategoriaDTO>> call, Response<List<CategoriaDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCategorias = new ArrayList<>(response.body());
                    CategoriaDTO placeholder = new CategoriaDTO() { @Override public String toString() { return CATEGORIA_PLACEHOLDER_TEXT; } };
                    listaCategorias.add(0, placeholder);
                    configurarSpinnerCategoria();
                } else { configurarSpinnerCategoriaErro(); }
            }
            @Override public void onFailure(Call<List<CategoriaDTO>> call, Throwable t) { configurarSpinnerCategoriaErro(); }
        });
    }

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
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { selectedCategoriaId = -1; }
        });
        spinnerCategoria.setSelection(0);
    }

    private void configurarSpinnerCategoriaErro() {
        List<String> erroList = new ArrayList<>(); erroList.add("Erro ao carregar");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, erroList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter); spinnerCategoria.setEnabled(false);
    }

    private void configurarSpinnerPrioridade() {
        final String[] prioridades = new String[]{PLACEHOLDER, "Urgente", "Alta", "Média", "Baixa"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, prioridades){
            @Override public boolean isEnabled(int position){ return position != 0; }
            @Override public View getDropDownView(int pos, View cv, @NonNull ViewGroup p) { View v = super.getDropDownView(pos, cv, p); TextView tv = (TextView) v; tv.setTextColor(pos == 0 ? Color.GRAY : Color.BLACK); return v; }
            @Override public View getView(int pos, View cv, ViewGroup p) { View v = super.getView(pos, cv, p); TextView tv = (TextView) v; tv.setTextColor(pos == 0 ? Color.GRAY : Color.BLACK); return v; }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(adapter);
        spinnerPrioridade.setSelection(0);
    }

    // --- Valida e Envia o Chamado ---
    private void validarEEnviarChamado() {
        String titulo = editTitulo.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();
        String prioridade = spinnerPrioridade.getSelectedItem().toString();
        int idCategoria = selectedCategoriaId;
        int idUsuarioAbertura = idUsuarioLogado;

        boolean houveErro = false;
        if (titulo.isEmpty()) { editTitulo.setError("Obrigatório."); houveErro = true; }
        if (email.isEmpty()) { editEmail.setError("Obrigatório."); houveErro = true; }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { editEmail.setError("E-mail inválido."); houveErro = true; }
        if (descricao.isEmpty()) { editDescricao.setError("Obrigatório."); houveErro = true; }
        if (prioridade.equals(PLACEHOLDER)) { Toast.makeText(this, "Selecione a Prioridade.", Toast.LENGTH_SHORT).show(); houveErro = true; }
        if (idCategoria <= 0) { Toast.makeText(this, "Selecione a Categoria.", Toast.LENGTH_SHORT).show(); houveErro = true; }
        if (idUsuarioAbertura <= 0) { Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_LONG).show(); houveErro = true; }

        if (!houveErro) {
            btnConfirmar.setEnabled(false);

            Call<AbrirChamadoResponse> call = apiService.abrirChamado(titulo, idUsuarioAbertura, prioridade, idCategoria, email, descricao);

            call.enqueue(new Callback<AbrirChamadoResponse>() {
                @Override
                public void onResponse(Call<AbrirChamadoResponse> call, Response<AbrirChamadoResponse> response) {
                    // btnConfirmar.setEnabled(true); // (Só reabilita se falhar, senão fecha a tela)
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        int novoIdChamado = response.body().getId_chamado();

                        // ✨ SE TIVER ARQUIVO, FAZ UPLOAD AGORA ✨
                        if (uriSelecionada != null) {
                            uploadArquivo(novoIdChamado, uriSelecionada);
                        } else {
                            Toast.makeText(AberturaChamadoActivity.this, "Chamado aberto com sucesso!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        btnConfirmar.setEnabled(true);
                        Toast.makeText(AberturaChamadoActivity.this, "Erro ao abrir chamado.", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<AbrirChamadoResponse> call, Throwable t) {
                    btnConfirmar.setEnabled(true);
                    Toast.makeText(AberturaChamadoActivity.this, "Falha de rede.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // --- ✨ MÉTODO NOVO PARA UPLOAD DE ARQUIVO ✨ ---
    private void uploadArquivo(int idChamado, Uri fileUri) {
        try {
            // 1. Prepara o arquivo usando FileUtils (Crie a classe utils se não tiver!)
            File file = com.example.solveit.utils.FileUtils.getFileFromUri(this, fileUri);

            // 2. Cria as partes do Multipart
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("arquivo", file.getName(), requestFile);
            RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idChamado));

            // 3. Chama API
            apiService.uploadArquivo(idBody, body).enqueue(new Callback<AtribuicaoResponse>() {
                @Override
                public void onResponse(Call<AtribuicaoResponse> call, Response<AtribuicaoResponse> response) {
                    Toast.makeText(AberturaChamadoActivity.this, "Chamado e arquivo enviados!", Toast.LENGTH_LONG).show();
                    finish();
                }
                @Override
                public void onFailure(Call<AtribuicaoResponse> call, Throwable t) {
                    Toast.makeText(AberturaChamadoActivity.this, "Chamado criado, mas erro no upload.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Erro arquivo: " + e.getMessage());
            Toast.makeText(this, "Erro ao processar arquivo.", Toast.LENGTH_SHORT).show();
            finish(); // Fecha mesmo com erro no arquivo, pois o chamado já foi criado
        }
    }

    private void abrirSeletorArquivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); intent.setType("*/*"); intent.addCategory(Intent.CATEGORY_OPENABLE);
        try { startActivityForResult(Intent.createChooser(intent, "Selecione o arquivo"), PICK_FILE_REQUEST_CODE); }
        catch (android.content.ActivityNotFoundException ex) { Toast.makeText(this, "Nenhum gerenciador encontrado.", Toast.LENGTH_SHORT).show(); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData(); String nomeArquivo = "Arquivo selecionado";
            if (uri != null) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) { nomeArquivo = cursor.getString(nameIndex); }
                    }
                } catch (Exception e) { Log.e(TAG, "Erro nome arquivo", e); }

                // ✨ GUARDA A URI NA VARIÁVEL GLOBAL ✨
                uriSelecionada = uri;
            }
            textNomeArquivo.setText(nomeArquivo); textNomeArquivo.setTextColor(Color.BLACK);
            Toast.makeText(this, "Arquivo: " + nomeArquivo, Toast.LENGTH_SHORT).show();
        }
    }
}