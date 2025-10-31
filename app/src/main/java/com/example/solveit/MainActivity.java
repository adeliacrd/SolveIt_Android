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

    private static final String TAG = "LoginApp"; // Tag para logs do Login

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


        // ----- CÓDIGO PARA O TEXTVIEW DE CADASTRO (permanece igual) -----
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


        // ----- CÓDIGO PARA O TEXTVIEW "ESQUECI A SENHA" (permanece igual) -----
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


        // Configurar o que acontece quando o botão de login é clicado
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v); // Chamar o método de login simulado
            }
        });
    }

    private void loginUser(View buttonView) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validações de campos (seu código já faz isso bem)
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

        // Mostra a barra de progresso e desabilita o botão
        progressBarLogin.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);

        // ======================================================================
        // INÍCIO DA LÓGICA REAL DE LOGIN (AQUI ESTÁ A MÁGICA!)
        // ======================================================================

        // 1. Obtenha a instância do serviço da API através do Retrofit
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // 2. Crie a chamada para o endpoint de login
        Call<LoginResponse> call = apiService.loginUsuario(email, password);

        // 3. Execute a chamada de forma assíncrona (em background)
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Esconde a barra de progresso e reabilita o botão
                progressBarLogin.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);

                // Verifica se a resposta do servidor foi bem-sucedida (código 200-299)
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        // SUCESSO! O backend validou o usuário.
                        Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                        // Navega para a próxima tela
                        Intent homeIntent = new Intent(MainActivity.this, ListaChamadosActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        // Falha de negócio (ex: senha errada)
                        Toast.makeText(MainActivity.this, "Erro: " + loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Erro do servidor (ex: 401 Unauthorized, 404 Not Found)
                    Toast.makeText(MainActivity.this, "Credenciais inválidas ou erro no servidor.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Esconde a barra de progresso e reabilita o botão
                progressBarLogin.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);

                // Erro de rede (sem internet, firewall, servidor offline)
                Log.e(TAG, "Falha na chamada de rede: ", t);
                Toast.makeText(MainActivity.this, "Não foi possível conectar ao servidor. Verifique sua conexão.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
