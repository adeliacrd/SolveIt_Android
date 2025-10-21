package com.example.solveit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.example.solveit.api.ApiService;
import com.example.solveit.api.RegisterResponse;
import com.example.solveit.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.lang.Character;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegistroApp";

    // Variáveis para os componentes da tela
    private EditText editTextFullName, editTextCPF, editTextEmail, editTextCompanyName, editTextPassword, editTextConfirmPassword;
    private Spinner spinnerLoginOptions;
    private ProgressBar progressBarRegister;
    private Button buttonRegister;

    // Variáveis de controle
    private ApiService apiService;
    private String selectedLogin = "";
    private int defaultSpinnerTextColor;
    private float defaultSpinnerTextSizeSp = 12f;
    private final String LOGIN_PROMPT = "Escolha um login ou digite o seu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // --- Conectando as variáveis do Java com os IDs do XML ---
        editTextFullName = findViewById(R.id.editTextName);
        editTextCPF = findViewById(R.id.editTextCPF);
        editTextEmail = findViewById(R.id.editTextEmailCorporativo);
        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        spinnerLoginOptions = findViewById(R.id.spinnerLoginOptions);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        progressBarRegister = findViewById(R.id.progressBarRegister);
        buttonRegister = findViewById(R.id.buttonCreateAccount);

        // --- Captura da Cor e Tamanho Padrão ---
        try {
            ContextThemeWrapper themedContext = new ContextThemeWrapper(this, R.style.Theme_SolveIt);
            TextView tempThemedTextView = new TextView(themedContext);
            defaultSpinnerTextColor = tempThemedTextView.getCurrentTextColor();
            if (defaultSpinnerTextColor == 0 || defaultSpinnerTextColor == Color.TRANSPARENT) { defaultSpinnerTextColor = Color.BLACK; }
            float editTextSizePx = editTextFullName.getTextSize(); float scale = getResources().getDisplayMetrics().scaledDensity;
            defaultSpinnerTextSizeSp = editTextSizePx / scale; if (defaultSpinnerTextSizeSp <= 0) defaultSpinnerTextSizeSp = 12f;
        } catch (Exception e) { defaultSpinnerTextColor = Color.BLACK; defaultSpinnerTextSizeSp = 12f; }


        // --- Ouvinte para o NOME (gera logins) ---
        editTextFullName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { gerarLoginsSugeridos(); }
        });

        // --- Máscara do CPF ---
        editTextCPF.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (isUpdating) { isUpdating = false; return; }
                String cleanString = s.toString().replaceAll("[^0-9]", ""); String formattedCpf = ""; int currentPosition = 0;
                try {
                    for (int i = 0; i < cleanString.length(); i++) {
                        if (currentPosition == 3 || currentPosition == 7) { formattedCpf += "."; currentPosition++; }
                        else if (currentPosition == 11) { formattedCpf += "-"; currentPosition++; }
                        formattedCpf += cleanString.charAt(i); currentPosition++;
                    }
                    if (formattedCpf.length() > 14) formattedCpf = formattedCpf.substring(0, 14);
                    isUpdating = true; editTextCPF.setText(formattedCpf); editTextCPF.setSelection(formattedCpf.length());
                } catch (Exception e) { isUpdating = true; editTextCPF.setText(cleanString); editTextCPF.setSelection(cleanString.length()); }
            }
        });

        // --- Ouvinte do Spinner ---
        spinnerLoginOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelecionado = parent.getItemAtPosition(position).toString();
                TextView selectedTextView = null;
                if (view instanceof TextView) { selectedTextView = (TextView) view; }
                if (selectedTextView == null) { selectedLogin = itemSelecionado; return; }

                selectedTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultSpinnerTextSizeSp);

                if (position == 0 && itemSelecionado.equals(LOGIN_PROMPT)) {
                    selectedLogin = ""; selectedTextView.setTextColor(Color.GRAY);
                } else {
                    selectedLogin = itemSelecionado; selectedTextView.setTextColor(defaultSpinnerTextColor); // Cor Padrão (Preto)
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { selectedLogin = ""; }
        });

        // --- Clique do botão ---
        buttonRegister.setOnClickListener(v -> registerUser());

        // --- Inicia o spinner com o "hint" ---
        gerarLoginsSugeridos();
    }

    /**
     * Este método é chamado quando o botão de registro é clicado.
     * Ele VALIDA e FORMATA os dados da tela e envia para a API.
     */
    private void registerUser() {
        Log.d(TAG, "Botão de registro clicado. Iniciando validações.");

        // --- Coleta de dados ---
        String fullNameInput = editTextFullName.getText().toString().trim();
        String cpfInput = editTextCPF.getText().toString().trim();
        String emailInput = editTextEmail.getText().toString().trim();
        String companyNameInput = editTextCompanyName.getText().toString().trim();
        String loginSugeridoInput = selectedLogin;
        String passwordInput = editTextPassword.getText().toString().trim();
        String confirmPasswordInput = editTextConfirmPassword.getText().toString().trim();

        // --- VALIDAÇÕES E FORMATAÇÕES ---

        // 1. Campos Vazios
        if (fullNameInput.isEmpty() || cpfInput.isEmpty() || emailInput.isEmpty() || companyNameInput.isEmpty() || loginSugeridoInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show(); return;
        }
        if (loginSugeridoInput.equals(LOGIN_PROMPT)) {
            Toast.makeText(this, "Por favor, escolha um login sugerido.", Toast.LENGTH_LONG).show(); return;
        }

        // 2. Formato do E-mail
        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            editTextEmail.setError("Formato de e-mail inválido."); editTextEmail.requestFocus(); return;
        }

        // 3. Formato do CPF
        String cpfNumeros = cpfInput.replaceAll("[^0-9]", "");
        if (cpfNumeros.length() != 11) {
            editTextCPF.setError("CPF inválido (deve ter 11 dígitos)."); editTextCPF.requestFocus(); return;
        }

        // 4. Tamanho Mínimo da Senha
        if (passwordInput.length() < 6) {
            editTextPassword.setError("A senha deve ter no mínimo 6 caracteres."); editTextPassword.requestFocus(); return;
        }

        // 5. Confirmação de Senha
        if (!passwordInput.equals(confirmPasswordInput)) {
            editTextConfirmPassword.setError("As senhas não coincidem."); editTextConfirmPassword.requestFocus(); return;
        }

        // 6. Capitalização do Nome (Formatação)
        String fullNameFormatted = capitalizeWords(fullNameInput);

        // ✨ 7. CAPITALIZAÇÃO DO NOME DA EMPRESA (Formatação) ✨
        // Removemos a validação antiga e apenas formatamos o nome.
        String companyNameFormatted = capitalizeWords(companyNameInput);
        // (Opcional) Atualiza o campo visualmente ou apenas envia o formatado
        // editTextCompanyName.setText(companyNameFormatted); // Descomente se quiser mostrar formatado

        // --- FIM DAS VALIDAÇÕES ---

        Log.d(TAG, "Validações passaram. Enviando para a API.");

        progressBarRegister.setVisibility(View.VISIBLE);
        buttonRegister.setEnabled(false);

        // Chamada à API (Usando os dados validados e formatados)
        Call<RegisterResponse> call = apiService.registerUsuario(
                fullNameFormatted,
                cpfNumeros,
                emailInput,
                companyNameFormatted, // ✨ Envia o nome da empresa formatado ✨
                loginSugeridoInput,
                passwordInput
        );

        // Lógica de Callback (onResponse/onFailure) - Sem alterações
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                progressBarRegister.setVisibility(View.GONE); buttonRegister.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class); startActivity(intent); finish();
                    } else { Toast.makeText(RegisterActivity.this, "Erro: " + registerResponse.getMessage(), Toast.LENGTH_LONG).show(); }
                } else {
                    String erroMsg = "Erro no servidor (Código: " + response.code() + ").";
                    if (response.code() == 409) { erroMsg = "Este e-mail ou CPF já está cadastrado."; }
                    Toast.makeText(RegisterActivity.this, erroMsg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                progressBarRegister.setVisibility(View.GONE); buttonRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Falha de conexão. Verifique a internet ou o servidor.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Erro de rede no registro: ", t);
            }
        });
    }

    // --- Método gerarLoginsSugeridos() ---
    // (Lógica com números aleatórios, tamanho e cor do hint)
    private void gerarLoginsSugeridos() {
        String nomeCompleto = editTextFullName.getText().toString().trim().toLowerCase().replaceAll("\\s+", " ");
        List<String> sugestoes = new ArrayList<>();
        sugestoes.add(LOGIN_PROMPT);

        if (!nomeCompleto.isEmpty()) {
            String[] partesNome = nomeCompleto.split(" "); String primeiroNome = partesNome[0]; String ultimoNome = (partesNome.length > 1) ? partesNome[partesNome.length - 1] : null; String inicial = String.valueOf(primeiroNome.charAt(0)); Random random = new Random(); String sufixoNumerico = String.format("%03d", random.nextInt(1000));
            if (ultimoNome != null) {
                sugestoes.add(primeiroNome + ultimoNome); sugestoes.add(primeiroNome + "." + ultimoNome); sugestoes.add(primeiroNome + "_" + ultimoNome); sugestoes.add(inicial + "." + ultimoNome); sugestoes.add(inicial + "_" + ultimoNome); sugestoes.add(primeiroNome + "." + ultimoNome + sufixoNumerico); sugestoes.add(primeiroNome + "_" + ultimoNome + sufixoNumerico);
            } else { sugestoes.add(primeiroNome + sufixoNumerico); }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, sugestoes );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); spinnerLoginOptions.setAdapter(adapter);

        spinnerLoginOptions.post(() -> {
            View initialView = spinnerLoginOptions.getSelectedView();
            if (initialView instanceof TextView) {
                TextView initialTextView = (TextView) initialView; initialTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultSpinnerTextSizeSp);
                if (spinnerLoginOptions.getSelectedItemPosition() == 0) { initialTextView.setTextColor(Color.GRAY); }
                else { initialTextView.setTextColor(defaultSpinnerTextColor); }
            }
        });
        spinnerLoginOptions.setSelection(0, false);
    }

    // --- Método auxiliar para capitalizar nomes ---
    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) { return str; }
        String[] words = str.trim().split("\\s+"); StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                String firstLetter = word.substring(0, 1).toUpperCase();
                String rest = (word.length() > 1) ? word.substring(1).toLowerCase() : "";
                capitalized.append(firstLetter).append(rest).append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}