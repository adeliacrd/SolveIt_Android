package com.example.solveit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solveit.api.ApiService;
import com.example.solveit.api.RegisterResponse;
import com.example.solveit.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Seu código do Spinner precisa desses imports também, se já não estiverem lá
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.Color;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegistroApp";

    // Variáveis para os componentes da tela
    private EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextCompanyName;
    private Spinner spinnerCompanySize;
    private ProgressBar progressBarRegister;
    private Button buttonRegister; // A variável para o botão

    // Variável para a API
    private ApiService apiService;

    // Variáveis para a lógica do Spinner
    private String selectedCompanySize = "";
    private int defaultSpinnerTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- Inicialização da API (seu código já estava correto) ---
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // =========================================================================
        // <<< CORREÇÃO PRINCIPAL AQUI >>>
        // Conectando cada variável Java com o ID exato do seu arquivo XML.
        // =========================================================================
        editTextFullName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmailCorporativo);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        spinnerCompanySize = findViewById(R.id.spinnerCompanySize);
        progressBarRegister = findViewById(R.id.progressBarRegister);

        // <<< O ID no XML é "buttonCreateAccount", então usamos ele aqui >>>
        buttonRegister = findViewById(R.id.buttonCreateAccount);

        // --- Configuração do Spinner (movi sua lógica para um método separado) ---
        setupSpinner();

        // --- Configuração do Clique do Botão (o jeito moderno e seguro) ---
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quando o botão for clicado, chame o método para registrar o usuário
                registerUser();
            }
        });
    }

    /**
     * Este método é chamado quando o botão de registro é clicado.
     * Ele coleta os dados da tela e envia para a API.
     */
    private void registerUser() {
        Log.d(TAG, "Botão de registro clicado. Iniciando processo.");

        // Coleta dos dados dos campos
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String companyName = editTextCompanyName.getText().toString().trim();
        String companySize = selectedCompanySize;

        // VALIDAÇÕES LOCAIS (verificando se os campos não estão vazios, etc.)
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || companyName.isEmpty() || companySize.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("As senhas não coincidem.");
            editTextConfirmPassword.requestFocus();
            return;
        }

        // Mostrar a barra de progresso e desabilitar o botão para evitar cliques duplos
        progressBarRegister.setVisibility(View.VISIBLE);
        buttonRegister.setEnabled(false);

        // CHAMADA À API USANDO RETROFIT
        Call<RegisterResponse> call = apiService.registerUsuario(fullName, email, password, companyName, companySize);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                // Esconder a barra e reabilitar o botão, não importa o resultado
                progressBarRegister.setVisibility(View.GONE);
                buttonRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // O servidor respondeu com sucesso (código 200-299)
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Fecha a tela de registro
                    } else {
                        // O servidor respondeu com um erro de lógica (ex: email já existe)
                        Toast.makeText(RegisterActivity.this, "Erro: " + registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // O servidor respondeu com um erro HTTP (ex: 409, 500)
                    String erroMsg = "Erro no servidor (Código: " + response.code() + ").";
                    if (response.code() == 409) {
                        erroMsg = "Este e-mail já está cadastrado.";
                    }
                    Toast.makeText(RegisterActivity.this, erroMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Erro de conexão (sem internet, servidor offline)
                progressBarRegister.setVisibility(View.GONE);
                buttonRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Falha de conexão. Verifique a internet ou o servidor.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Erro de rede no registro: ", t);
            }
        });
    }

    /**
     * Método que contém toda a sua lógica para configurar o Spinner.
     * Fica mais organizado assim.
     */
    private void setupSpinner() {
        String[] companySizeOptions = getResources().getStringArray(R.array.company_size_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_selected_item, companySizeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCompanySize.setAdapter(adapter);

        if (spinnerCompanySize.getSelectedView() != null) {
            defaultSpinnerTextColor = ((TextView) spinnerCompanySize.getSelectedView()).getCurrentTextColor();
        } else {
            defaultSpinnerTextColor = Color.BLACK;
        }

        spinnerCompanySize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedTextView = (TextView) view;
                if (position == 0) {
                    selectedCompanySize = ""; // Valor vazio se a primeira opção for selecionada
                    if (selectedTextView != null) selectedTextView.setTextColor(Color.GRAY);
                } else {
                    selectedCompanySize = parent.getItemAtPosition(position).toString();
                    if (selectedTextView != null) selectedTextView.setTextColor(defaultSpinnerTextColor);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCompanySize = "";
            }
        });
    }
}