// CÓDIGO REESCRITO COM A LÓGICA DE ADMIN

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

        // A lógica para os links de "Criar Conta" e "Esqueci a Senha" permanece a mesma.
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

        // Configurar o que acontece quando o botão de login é clicado
        buttonLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validações de campos
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

        // Lógica de Login com Retrofit
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
                        // SUCESSO! O backend validou o usuário.
                        Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                        // ======================================================================
                        // ✨✨✨ SUBSTITUIÇÃO DA NAVEGAÇÃO PARA INCLUIR A LÓGICA DE ADMIN ✨✨✨
                        // ======================================================================

                        // 1. Verifique se o usuário é um admin.
                        //    (Estou assumindo que a sua classe LoginResponse tem um método 'isAdmin()')
                        boolean ehAdmin = loginResponse.isAdmin();

                        // 2. Crie a Intent para ir para a lista de chamados.
                        Intent homeIntent = new Intent(MainActivity.this, ListaChamadosActivity.class);

                        // 3. Adicione a "etiqueta" booleana que diz se é um admin ou não.
                        homeIntent.putExtra("IS_ADMIN", ehAdmin);

                        // 4. Limpe as telas anteriores e inicie a nova atividade com a etiqueta.
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish(); // Fecha a tela de login para o usuário não voltar para ela

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
                progressBarLogin.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);
                Log.e(TAG, "Falha na chamada de rede: ", t);
                Toast.makeText(MainActivity.this, "Não foi possível conectar ao servidor. Verifique sua conexão.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
