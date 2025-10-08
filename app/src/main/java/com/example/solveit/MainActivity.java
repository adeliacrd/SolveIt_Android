package com.example.solveit; // !! Verifique o package name !!

import androidx.annotation.NonNull; // <<< ADICIONAR IMPORT
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar; // <<< ADICIONAR IMPORT
import android.widget.TextView;
import android.widget.Toast;

// Firebase Auth imports
import com.google.android.gms.tasks.OnCompleteListener; // <<< ADICIONAR IMPORT
import com.google.android.gms.tasks.Task; // <<< ADICIONAR IMPORT
import com.google.firebase.auth.AuthResult; // <<< ADICIONAR IMPORT
import com.google.firebase.auth.FirebaseAuth; // <<< ADICIONAR IMPORT
import com.google.firebase.auth.FirebaseUser; // <<< ADICIONAR IMPORT
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException; // <<< ADICIONAR IMPORT
import com.google.firebase.auth.FirebaseAuthInvalidUserException; // <<< ADICIONAR IMPORT


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginApp"; // Tag para logs do Login

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewGoToRegister;
    private TextView textViewForgotPasswordLink;
    private ProgressBar progressBarLogin; // <<< ADICIONADO: ProgressBar

    private FirebaseAuth mAuth; // <<< ADICIONADO: Instância do Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance(); // <<< ADICIONADO

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewGoToRegister = findViewById(R.id.textViewCreateAccount);
        textViewForgotPasswordLink = findViewById(R.id.textViewForgotPassword);
        // Assumindo que você tem um ProgressBar com id 'progressBarLogin' no seu activity_main.xml
        // Certifique-se que o ID 'progressBarLogin' existe no seu activity_main.xml
        progressBarLogin = findViewById(R.id.progressBarLogin); // <<< ADICIONADO


        // ----- INÍCIO DO CÓDIGO PARA O TEXTVIEW DE CADASTRO -----
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
        // ----- FIM DO CÓDIGO PARA O TEXTVIEW DE CADASTRO -----


        // ----- INÍCIO DO CÓDIGO PARA O TEXTVIEW "ESQUECI A SENHA" -----
        if (textViewForgotPasswordLink != null) {
            textViewForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crie e navegue para ForgotPasswordActivity se ela existir
                    Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e(TAG, "TextView 'textViewForgotPassword' não foi encontrado.");
        }
        // ----- FIM DO CÓDIGO PARA O TEXTVIEW "ESQUECI A SENHA" -----


        // Configurar o que acontece quando o botão de login é clicado
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v); // Chamar o método de login
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

        Log.d(TAG, "Tentativa de login com email: " + email);

        // Mostrar ProgressBar e desabilitar botão
        if (progressBarLogin != null) {
            progressBarLogin.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "progressBarLogin é NULO ao tentar tornar VISIBLE");
        }
        if (buttonView != null) {
            buttonView.setEnabled(false);
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Esconder ProgressBar e reabilitar botão
                        if (progressBarLogin != null) {
                            progressBarLogin.setVisibility(View.GONE);
                        }
                        if (buttonView != null) {
                            buttonView.setEnabled(true);
                        }

                        if (task.isSuccessful()) {
                            // Login bem-sucedido
                            Log.d(TAG, "signInWithEmail:SUCESSO");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                            // Navegar para a próxima tela (AGORA VAI PARA SUA TELA DE TESTE!)
                            Intent homeIntent = new Intent(MainActivity.this, AberturaChamadoActivity.class);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeIntent);
                            finish(); // Finaliza MainActivity para não poder voltar para ela com o botão "back"
                        } else {
                            // Se o login falhar
                            Log.w(TAG, "signInWithEmail:FALHA", task.getException());
                            String errorMessage = "Falha na autenticação."; // Mensagem padrão
                            try {
                                // É importante lançar a exceção para poder capturá-la pelos tipos específicos
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                errorMessage = "Não há conta registrada com este email.";
                                editTextEmail.setError(errorMessage); // Opcional: mostrar erro no campo
                                editTextEmail.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                errorMessage = "Senha incorreta. Por favor, tente novamente.";
                                editTextPassword.setError(errorMessage); // Opcional: mostrar erro no campo
                                editTextPassword.requestFocus();
                            } catch (Exception e) {
                                // Para outras exceções (rede, problemas no SDK, etc.)
                                if (e.getMessage() != null && !e.getMessage().isEmpty()){
                                    errorMessage = e.getMessage();
                                }
                                Log.e(TAG, "Erro de login não tratado: " + e.getMessage());
                            }
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Opcional: Verificar se o usuário já está logado ao iniciar a Activity
    @Override
    protected void onStart() {
        super.onStart();
        // Verifique se mAuth foi inicializado
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Usuário já logado: " + currentUser.getEmail() + ". Considerar redirecionamento.");
            // Se já estiver logado, você pode querer ir direto para a tela principal
            // Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            // homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(homeIntent);
            // finish();
        }
    }
}
