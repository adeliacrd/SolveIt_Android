// Copie e cole este código inteiro para C:/Users/adeli/StudioProjects/SolveIt/app/src/main/java/com/example/solveit/MainActivity.java

package com.example.solveit;

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
import com.example.solveit.api.ApiService;
import com.example.solveit.api.LoginResponse;
import com.example.solveit.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "SolveItPrefs";
    private static final String TAG = "LoginApp";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewGoToRegister;
    private TextView textViewForgotPasswordLink;
    private ProgressBar progressBarLogin;

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
            textViewGoToRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e(TAG, "TextView 'textViewCreateAccount' não foi encontrado.");
        }

        if (textViewForgotPasswordLink != null) {
            textViewForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e(TAG, "TextView 'textViewForgotPassword' não foi encontrado.");
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    // MÉTODO REESCRITO COM AS LINHAS DE DEPURAÇÃO
    private void performLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // ----------------- INÍCIO DO CÓDIGO DE DEPURAÇÃO -----------------
        // Vamos imprimir os valores exatos que estamos recebendo e comparando.
        Log.d("DEBUG_LOGIN", "--- Verificando Login Mágico ---");
        Log.d("DEBUG_LOGIN", "E-mail digitado: [" + email + "]");
        Log.d("DEBUG_LOGIN", "Senha digitada: [" + password + "]");
        // ------------------ FIM DO CÓDIGO DE DEPURAÇÃO -------------------

        if (email.isEmpty()) {
            editTextEmail.setError("Por favor, insira o email.");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Por favor, insira a senha.");
            editTextPassword.requestFocus();
            return;
        }

        // ======================================================================
        //                  INÍCIO DO CÓDIGO MÁGICO TEMPORÁRIO
        // ======================================================================
        if ("admin".equals(email) && "123".equals(password)) {
            // ----------- CÓDIGO DE DEPURAÇÃO DE SUCESSO -----------
            Log.d("DEBUG_LOGIN", "SUCESSO! A condição IF foi satisfeita. Entrando no app...");
            // --------------------------------------------------------

            Toast.makeText(MainActivity.this, "Login de Desenvolvedor Ativado!", Toast.LENGTH_LONG).show();

            Intent homeIntent = new Intent(MainActivity.this, ListaChamadosActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            finish();

            return;
        }
        // ======================================================================
        //                   FIM DO CÓDIGO MÁGICO TEMPORÁRIO
        // ======================================================================

        // ----------- CÓDIGO DE DEPURAÇÃO DE FALHA -----------
        Log.d("DEBUG_LOGIN", "FALHA! A condição IF não foi satisfeita. Prosseguindo para o login real via servidor.");
        // ----------------------------------------------------

        // O código abaixo só será executado se o login mágico NÃO for usado.

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
                        Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                        Intent homeIntent = new Intent(MainActivity.this, ListaChamadosActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Erro: " + loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Credenciais inválidas ou erro no servidor.", Toast.LENGTH_LONG).show();
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
