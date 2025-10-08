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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Auth imports
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordApp"; // Tag para logs

    private TextInputEditText editTextEmailForgotPassword;
    private Button buttonSendResetEmail;
    private TextView textViewBackToLogin;
    private ProgressBar progressBarForgotPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // CORREÇÃO: Removendo o EdgeToEdge e WindowInsets (CAUSADORES DO CRASH)
        setContentView(R.layout.activity_forgot_password);

        // Inicializa os componentes da UI
        editTextEmailForgotPassword = findViewById(R.id.editTextEmailForgotPassword);
        buttonSendResetEmail = findViewById(R.id.buttonSendResetEmail);
        textViewBackToLogin = findViewById(R.id.textViewBackToLogin);
        progressBarForgotPassword = findViewById(R.id.progressBarForgotPassword);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configura o listener para o botão de enviar e-mail de redefinição
        buttonSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmailRequest(v); // Chamar o método refatorado
            }
        });

        // Configura o listener para o texto "Voltar para o Login"
        textViewBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Simplesmente fecha esta activity
            }
        });
    }

    private void sendPasswordResetEmailRequest(View buttonView) {
        String email = editTextEmailForgotPassword.getText().toString().trim();

        // Validação básica do e-mail
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

        Log.d(TAG, "Tentando enviar email de redefinição para: " + email);

        // Mostrar ProgressBar e desabilitar botão
        if (progressBarForgotPassword != null) {
            progressBarForgotPassword.setVisibility(View.VISIBLE);
        }
        if (buttonView != null) {
            buttonView.setEnabled(false);
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Esconder ProgressBar e reabilitar botão
                        if (progressBarForgotPassword != null) {
                            progressBarForgotPassword.setVisibility(View.GONE);
                        }
                        if (buttonView != null) {
                            buttonView.setEnabled(true);
                        }

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email de redefinição enviado com sucesso para: " + email);
                            // --- MENSAGEM DO TOAST DE SUCESSO AJUSTADA ---
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Link enviado. Verifique seu e-mail.",
                                    Toast.LENGTH_LONG).show();
                            // ----------------------------------------------
                            // Voltando para o Login
                            Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "Falha ao enviar email de redefinição.", task.getException());
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Falha ao enviar o e-mail de redefinição. Verifique o e-mail ou tente mais tarde.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

