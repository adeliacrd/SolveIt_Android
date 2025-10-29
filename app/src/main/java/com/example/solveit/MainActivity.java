// Copie e cole este código inteiro para C:/Users/adeli/StudioProjects/SolveIt/app/src/main/java/com/example/solveit/MainActivity.java

package com.example.solveit;

// Imports necessários
import java.io.IOException;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
// Imports do Retrofit
import com.example.solveit.api.ApiService;
import com.example.solveit.api.LoginResponse;
import com.example.solveit.api.RetrofitClient;
import com.google.gson.Gson; // ✨ IMPORT NECESSÁRIO (para o log de erro) ✨
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginApp";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewGoToRegister;
    private TextView textViewForgotPasswordLink;
    private ProgressBar progressBarLogin;

    // ✨ Constantes para as chaves do SharedPreferences (CORRETO) ✨
    public static final String PREFS_NAME = "AppPrefs";
    public static final String KEY_USER_ID = "ID_USUARIO_LOGADO";
    public static final String KEY_USER_NAME = "NOME_USUARIO_LOGADO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewGoToRegister = findViewById(R.id.textViewCreateAccount);
        textViewForgotPasswordLink = findViewById(R.id.textViewForgotPassword);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        // --- Listeners para ir para Registro e Esqueci Senha (CORRETO) ---
        if (textViewGoToRegister != null) {
            textViewGoToRegister.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
        if (textViewForgotPasswordLink != null) {
            textViewForgotPasswordLink.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });
        }

        // --- Listener do Botão de Login (CORRETO) ---
        buttonLogin.setOnClickListener(v -> loginUser());
    }

    // ✨ Método de Login com a lógica correta (CORRETO) ✨
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validações
        if (email.isEmpty()) { editTextEmail.setError("Por favor, insira o email."); editTextEmail.requestFocus(); return; }
        if (password.isEmpty()) { editTextPassword.setError("Por favor, insira a senha."); editTextPassword.requestFocus(); return; }

        progressBarLogin.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);

        // --- Chamada da API ---
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.loginUsuario(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBarLogin.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                        // ==========================================================
                        // ✨ LÓGICA DE SALVAR DADOS DO USUÁRIO (A MUDANÇA IMPORTANTE) ✨
                        // ==========================================================
                        Integer idUsuario = loginResponse.getIdAcesso(); // Pega o ID
                        String nomeUsuario = loginResponse.getNomeUsuario(); // Pega o NOME

                        if (idUsuario != null && idUsuario > 0 && nomeUsuario != null && !nomeUsuario.isEmpty()) {
                            Log.d(TAG, "Salvando dados do usuário: ID=" + idUsuario + ", Nome=" + nomeUsuario);
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(KEY_USER_ID, idUsuario);
                            editor.putString(KEY_USER_NAME, nomeUsuario);
                            editor.apply();

                            // Navega para a próxima tela APENAS se salvou os dados
                            Intent homeIntent = new Intent(MainActivity.this, ListaChamadosActivity.class);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeIntent);
                            finish(); // Fecha a MainActivity

                        } else {
                            // Se os dados não vieram, loga um erro e informa o usuário
                            Log.e(TAG, "Erro: ID ou Nome do usuário não recebidos na resposta do login! Resposta: " + new Gson().toJson(loginResponse)); // Loga a resposta completa
                            Toast.makeText(MainActivity.this, "Erro ao obter dados do usuário após login. Verifique a API.", Toast.LENGTH_LONG).show();
                            // Não navega se não tiver os dados
                        }
                        // ==========================================================

                    } else { // Falha de negócio (senha errada, etc.)
                        Toast.makeText(MainActivity.this, "Erro: " + loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else { // Erro do servidor (4xx, 5xx)
                    String errorMsg = "Credenciais inválidas ou erro no servidor.";
                    if (response.errorBody() != null) {
                        try { errorMsg += " (" + response.code() + ": " + response.errorBody().string() + ")"; } catch (IOException e) {Log.e(TAG, "Erro ao ler errorBody", e);}
                    } else { errorMsg += " (Código: " + response.code() + ")"; }
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBarLogin.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);
                Log.e(TAG, "Falha na chamada de rede: ", t);
                Toast.makeText(MainActivity.this, "Não foi possível conectar ao servidor. Verifique sua conexão.", Toast.LENGTH_LONG).show();
            }
        });
    }
}