// Copie e cole este código inteiro para C:/Users/adeli/StudioProjects/SolveIt/app/src/main/java/com/example/solveit/ForgotPasswordActivity.java

package com.example.solveit;

// Imports
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

// Imports androidx
import androidx.appcompat.app.AppCompatActivity;

// Os imports do Firebase foram REMOVIDOS daqui

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordApp"; // Tag para logs

    private TextInputEditText editTextEmailForgotPassword;
    private Button buttonSendResetEmail;
    private TextView textViewBackToLogin;
    private ProgressBar progressBarForgotPassword;

    // A variável do Firebase (mAuth) foi REMOVIDA daqui

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Inicializa os componentes da UI
        editTextEmailForgotPassword = findViewById(R.id.editTextEmailForgotPassword);
        buttonSendResetEmail = findViewById(R.id.buttonSendResetEmail);
        textViewBackToLogin = findViewById(R.id.textViewBackToLogin);
        progressBarForgotPassword = findViewById(R.id.progressBarForgotPassword);

        // A inicialização do Firebase foi REMOVIDA daqui

        // Configura o listener para o botão de enviar e-mail de redefinição
        buttonSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmailRequest(v); // Chama o nosso novo método simulado
            }
        });

        // Configura o listener para o texto "Voltar para o Login"
        textViewBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Simplesmente fecha esta activity e volta para a anterior
            }
        });
    }

    private void sendPasswordResetEmailRequest(View buttonView) {
        String email = editTextEmailForgotPassword.getText().toString().trim();

        // Validação básica do e-mail (permanece igual)
        if (email.isEmpty()) {
            editTextEmailForgotPassword.setError("E-mail é obrigatório.");
            editTextEmailForgotPassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailForgotPassword.setError("Por favor, insira um e-mail válido.");
            editTextEmailForgotPassword.requestFocus();
            return;
        }

        Log.d(TAG, "Simulando envio de email de redefinição para: " + email);

        // Mostrar ProgressBar e desabilitar botão
        if (progressBarForgotPassword != null) {
            progressBarForgotPassword.setVisibility(View.VISIBLE);
        }
        if (buttonView != null) {
            buttonView.setEnabled(false);
        }

        // <<< LÓGICA DO FIREBASE FOI REMOVIDA DAQUI >>>
        // Agora, apenas mostramos uma mensagem de sucesso e voltamos para a tela de login.
        // Isso remove os erros e deixa o fluxo do app funcional.

        // Esconder ProgressBar e reabilitar botão (simulando que a tarefa terminou)
        if (progressBarForgotPassword != null) {
            progressBarForgotPassword.setVisibility(View.GONE);
        }
        if (buttonView != null) {
            buttonView.setEnabled(true);
        }

        Toast.makeText(ForgotPasswordActivity.this,
                "Se o e-mail estiver cadastrado, um link será enviado (simulação).",
                Toast.LENGTH_LONG).show();

        // Voltando para a tela de Login
        Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
