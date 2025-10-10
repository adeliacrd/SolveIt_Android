// Copie e cole este código inteiro para C:/Users/adeli/StudioProjects/SolveIt/app/src/main/java/com/example/solveit/RegisterActivity.java

package com.example.solveit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    private ProgressBar progressBarRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextFullName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmailCorporativo);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        spinnerCompanySize = findViewById(R.id.spinnerCompanySize);
        progressBarRegister = findViewById(R.id.progressBarRegister);

        // --- Configuração do Spinner (seu código ótimo permanece aqui) ---
        String[] companySizeOptions = getResources().getStringArray(R.array.company_size_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_selected_item, android.R.id.text1, companySizeOptions);
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
    }

    private void showProgressAndDisableButton(View buttonView) {
        progressBarRegister.setVisibility(View.VISIBLE);
        if (buttonView != null) buttonView.setEnabled(false);
    }

    private void hideProgressAndReEnableButton(View buttonView) {
        progressBarRegister.setVisibility(View.GONE);
        if (buttonView != null) buttonView.setEnabled(true);
    }

    public void onRegisterButtonClicked(View view) {
        Log.d(TAG, "onRegisterButtonClicked chamado.");
        showProgressAndDisableButton(view);

        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String companyName = editTextCompanyName.getText().toString().trim();

        // --- Validações (seu código ótimo permanece aqui) ---
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || companyName.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show();
            hideProgressAndReEnableButton(view);
            return;
        }
        String promptText = (getResources().getStringArray(R.array.company_size_options).length > 0) ? getResources().getStringArray(R.array.company_size_options)[0] : "";
        if (selectedCompanySize.isEmpty() || (spinnerCompanySize.getSelectedItemPosition() == 0 && spinnerCompanySize.getSelectedItem().toString().equals(promptText))) {
            Toast.makeText(this, "Por favor, selecione o tamanho da empresa.", Toast.LENGTH_LONG).show();
            hideProgressAndReEnableButton(view);
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_LONG).show();
            hideProgressAndReEnableButton(view);
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_LONG).show();
            hideProgressAndReEnableButton(view);
            return;
        }

        // <<< LÓGICA DE REGISTRO REMOVIDA >>>
        // Agora, o app não faz nada com os dados, mas também não dá erro.
        // Isso é o que queremos!

        Log.d(TAG, "Validações passaram. Nenhuma ação de registro configurada (isso é esperado por enquanto).");

        // Simula um sucesso e vai para a tela principal
        Toast.makeText(this, "Registro concluído (simulação).", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finaliza a tela de registro
    }
}
