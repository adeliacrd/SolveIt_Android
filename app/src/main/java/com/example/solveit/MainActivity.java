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

    private void loginUser(View buttonView) { // 'buttonView' é o botão de login
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        Log.d(TAG, "Simulando login com email: " + email);

        if (progressBarLogin != null) {
            progressBarLogin.setVisibility(View.VISIBLE);
        }
        if (buttonView != null) {
            buttonView.setEnabled(false);
        }

        Toast.makeText(MainActivity.this, "Login bem-sucedido (simulação)!", Toast.LENGTH_SHORT).show();

        // ======================================================================
        // A CORREÇÃO ESTÁ AQUI!
        // Navegar para a tela de LISTA de Chamados, em vez da de Abertura.
        // ======================================================================
        Intent homeIntent = new Intent(MainActivity.this, ListaChamadosActivity.class);

        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish(); // Finaliza MainActivity para não poder voltar para ela
    }
}
