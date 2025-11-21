package com.example.solveit;

// Imports necessários
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

    public static final String PREFS_NAME = "AppPrefs";
    public static final String KEY_USER_ID = "ID_USUARIO_LOGADO";
    public static final String KEY_USER_NAME = "NOME_USUARIO_LOGADO";
    public static final String KEY_USER_ROLE_ID = "ID_TIPO_ACESSO_LOGADO";

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

        buttonLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) { editTextEmail.setError("Por favor, insira o email."); editTextEmail.requestFocus(); return; }
        if (password.isEmpty()) { editTextPassword.setError("Por favor, insira a senha."); editTextPassword.requestFocus(); return; }

        progressBarLogin.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);

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
                        // --- SUCESSO NO LOGIN ---
                        Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                        Integer idUsuario = loginResponse.getIdAcesso();
                        String nomeUsuario = loginResponse.getNomeUsuario();
                        Integer idTipoAcesso = loginResponse.getIdTipoAcesso();

                        if (idUsuario != null && idUsuario > 0 && nomeUsuario != null && !nomeUsuario.isEmpty() && idTipoAcesso != null && idTipoAcesso > 0) {
                            Log.d(TAG, "Salvando dados do usuário: ID=" + idUsuario + ", Nome=" + nomeUsuario + ", Nível=" + idTipoAcesso);
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(KEY_USER_ID, idUsuario);
                            editor.putString(KEY_USER_NAME, nomeUsuario);
                            editor.putInt(KEY_USER_ROLE_ID, idTipoAcesso);
                            editor.apply();

                            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeIntent);
                            finish();

                        } else {
                            Log.e(TAG, "Erro: Dados do usuário (ID, Nome ou Nível) não recebidos na resposta do login!");
                            Toast.makeText(MainActivity.this, "Erro ao obter dados completos do usuário.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // ==========================================================
                        // ✨ AQUI ESTÁ A SUA CORREÇÃO ✨
                        // ==========================================================
                        // A API respondeu, mas o login falhou (isSuccess() == false).
                        // Exibimos a mensagem padrão que você pediu.
                        Toast.makeText(MainActivity.this, "Login ou senha incorreto!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // ==========================================================
                    // ✨ AQUI ESTÁ A SUA CORREÇÃO (Parte 2) ✨
                    // ==========================================================
                    // Erro de servidor (401, 404, etc.) também deve ser tratado como login incorreto.
                    Toast.makeText(MainActivity.this, "Usuário ou senha inválidos. Por favor, tente novamente", Toast.LENGTH_LONG).show();
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