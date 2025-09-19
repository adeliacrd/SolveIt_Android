package com.example.solveit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar; // <<< --- IMPORTAR ProgressBar
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegistroApp";

    private Spinner spinnerCompanySize;
    private String selectedCompanySize = "";
    private int defaultSpinnerTextColor;

    private EditText editTextFullName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextCompanyName;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ProgressBar progressBarRegister; // <<< --- DECLARAR ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextFullName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmailCorporativo);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        spinnerCompanySize = findViewById(R.id.spinnerCompanySize);

        progressBarRegister = findViewById(R.id.progressBarRegister); // <<< --- INICIALIZAR ProgressBar

        // Configuração do Spinner (seu código existente)
        String[] companySizeOptions = getResources().getStringArray(R.array.company_size_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_selected_item,
                android.R.id.text1,
                companySizeOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCompanySize.setAdapter(adapter);

        if (spinnerCompanySize.getSelectedItem() != null && spinnerCompanySize.getSelectedView() instanceof TextView) {
            defaultSpinnerTextColor = ((TextView) spinnerCompanySize.getSelectedView()).getCurrentTextColor();
        } else {
            TextView tempTextView = new TextView(this);
            tempTextView.setTextAppearance(android.R.style.TextAppearance_Widget_TextView_SpinnerItem);
            defaultSpinnerTextColor = tempTextView.getCurrentTextColor();
            if (defaultSpinnerTextColor == 0) defaultSpinnerTextColor = Color.BLACK;
        }

        spinnerCompanySize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelecionado = parent.getItemAtPosition(position).toString();
                String promptText = (companySizeOptions.length > 0) ? companySizeOptions[0] : "";
                TextView selectedTextView = (view instanceof TextView) ? (TextView) view : null;

                if (position == 0 && itemSelecionado.equals(promptText)) {
                    selectedCompanySize = "";
                    if (selectedTextView != null) selectedTextView.setTextColor(Color.GRAY);
                } else {
                    selectedCompanySize = itemSelecionado;
                    if (selectedTextView != null) selectedTextView.setTextColor(defaultSpinnerTextColor);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCompanySize = "";
                View currentSelectedView = spinnerCompanySize.getSelectedView();
                if (currentSelectedView instanceof TextView) {
                    ((TextView) currentSelectedView).setTextColor(Color.GRAY);
                }
            }
        });

        if (spinnerCompanySize.getCount() > 0) {
            spinnerCompanySize.setSelection(0);
            View selectedView = spinnerCompanySize.getSelectedView();
            if (selectedView instanceof TextView && companySizeOptions.length > 0 &&
                    ((TextView) selectedView).getText().toString().equals(companySizeOptions[0])) {
                ((TextView) selectedView).setTextColor(Color.GRAY);
            }
        }

        // TESTE RÁPIDO DE VISIBILIDADE (REMOVER APÓS TESTAR SE O PROGRESSBAR APARECE AO ABRIR A TELA)
        /*
        if (progressBarRegister != null) {
            progressBarRegister.setVisibility(View.VISIBLE);
            Log.d(TAG, "TESTE: ProgressBarRegister tornado VISIBLE no onCreate");
        } else {
            Log.e(TAG, "TESTE: progressBarRegister é NULO no onCreate");
        }
        */
    }

    private void showProgressAndDisableButton(View buttonView) {
        if (progressBarRegister != null) {
            progressBarRegister.setVisibility(View.VISIBLE);
            Log.d(TAG, "ProgressBarRegister tornado VISIBLE");
        } else {
            Log.e(TAG, "progressBarRegister é NULO ao tentar tornar VISIBLE");
        }
        if (buttonView != null) {
            buttonView.setEnabled(false);
        }
    }

    private void hideProgressAndReEnableButton(View buttonView) {
        if (progressBarRegister != null) {
            progressBarRegister.setVisibility(View.GONE);
            Log.d(TAG, "ProgressBarRegister tornado GONE (método auxiliar)");
        } else {
            Log.e(TAG, "progressBarRegister é NULO no método auxiliar hideProgressAndReEnableButton");
        }
        if (buttonView != null) {
            buttonView.setEnabled(true);
        }
    }

    public void onRegisterButtonClicked(View view) { // 'view' aqui é o botão que foi clicado
        Log.d(TAG, "onRegisterButtonClicked chamado.");

        showProgressAndDisableButton(view); // <<< --- MOSTRAR PROGRESSO E DESABILITAR BOTÃO

        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String companyName = editTextCompanyName.getText().toString().trim();

        // VALIDAÇÕES
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || companyName.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show();
            Log.w(TAG, "Algum campo obrigatório está vazio.");
            hideProgressAndReEnableButton(view); // <<< --- ESCONDER PROGRESSO
            return;
        }

        String promptText = (getResources().getStringArray(R.array.company_size_options).length > 0) ? getResources().getStringArray(R.array.company_size_options)[0] : "";
        if (selectedCompanySize.isEmpty() || (spinnerCompanySize.getSelectedItemPosition() == 0 && spinnerCompanySize.getSelectedItem().toString().equals(promptText))) {
            Toast.makeText(this, "Por favor, selecione o tamanho da empresa.", Toast.LENGTH_LONG).show();
            Log.w(TAG, "Tamanho da empresa não selecionado.");
            hideProgressAndReEnableButton(view); // <<< --- ESCONDER PROGRESSO
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_LONG).show();
            Log.w(TAG, "Senhas não coincidem.");
            editTextPassword.setError("Senhas não coincidem");
            editTextConfirmPassword.setError("Senhas não coincidem");
            hideProgressAndReEnableButton(view); // <<< --- ESCONDER PROGRESSO
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_LONG).show();
            Log.w(TAG, "Senha muito curta.");
            editTextPassword.setError("Mínimo 6 caracteres");
            hideProgressAndReEnableButton(view); // <<< --- ESCONDER PROGRESSO
            return;
        }

        Log.d(TAG, "Iniciando createUserWithEmailAndPassword para: " + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Auth onComplete. Sucesso: true");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                Log.d(TAG, "Usuário criado no Auth com sucesso. UID: " + userId);

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("fullName", fullName);
                                userData.put("email", email);
                                userData.put("companyName", companyName);
                                userData.put("companySize", selectedCompanySize);

                                Log.d(TAG, "Salvando dados do usuário no Firestore para UID: " + userId);
                                db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "SUCESSO ao salvar dados no Firestore para UID: " + userId);

                                                // Esconder o ProgressBar ANTES do Toast de sucesso final e do redirecionamento
                                                if (progressBarRegister != null) {
                                                    progressBarRegister.setVisibility(View.GONE);
                                                    Log.d(TAG, "ProgressBarRegister tornado GONE (sucesso)");
                                                } else {
                                                    Log.e(TAG, "progressBarRegister é NULO ao tentar tornar GONE (sucesso)");
                                                }
                                                // O botão não precisa ser reabilitado aqui pois a activity será finalizada.

                                                String mensagemSucesso = "Registro realizado com sucesso!";
                                                Toast.makeText(RegisterActivity.this, mensagemSucesso, Toast.LENGTH_LONG).show();
                                                Log.d(TAG, mensagemSucesso + " Redirecionando...");

                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "ERRO ao salvar dados no Firestore para UID: " + userId, e);
                                                Toast.makeText(RegisterActivity.this, "Erro ao salvar dados adicionais: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                hideProgressAndReEnableButton(view); // <<< --- ESCONDER PROGRESSO
                                                // Considerar deletar usuário do Auth aqui
                                            }
                                        });
                            } else {
                                Log.e(TAG, "firebaseUser é nulo após registro bem-sucedido no Auth.");
                                Toast.makeText(RegisterActivity.this, "Erro inesperado ao obter dados do usuário.", Toast.LENGTH_LONG).show();
                                hideProgressAndReEnableButton(view); // <<< --- ESCONDER PROGRESSO
                            }
                        } else {
                            Log.e(TAG, "Falha ao criar usuário no Auth: ", task.getException());
                            Toast.makeText(RegisterActivity.this, "Falha no registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            hideProgressAndReEnableButton(view); // <<< --- ESCONDER PROGRESSO
                        }
                    }
                });
    }
}
