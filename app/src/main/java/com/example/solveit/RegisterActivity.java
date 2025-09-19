package com.example.solveit;

import android.graphics.Color; // IMPORTAR A CLASSE COLOR
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView; // IMPORTAR TEXTVIEW
import android.widget.Toast;
// import android.view.ViewGroup; // Não estritamente necessário para esta mudança específica

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private Spinner spinnerCompanySize;
    private String selectedCompanySize = "";
    private int defaultSpinnerTextColor; // Para armazenar a cor padrão do texto do Spinner

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

        spinnerCompanySize = findViewById(R.id.spinnerCompanySize);

        String[] companySizeOptions = getResources().getStringArray(R.array.company_size_options);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_selected_item,
                android.R.id.text1,
                companySizeOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCompanySize.setAdapter(adapter);

        // Obtém a cor padrão do texto do spinner para restaurá-la depois
        // Fazemos isso uma vez para evitar recriar o objeto Color toda vez.
        // É importante que o Spinner já tenha um item selecionado (o prompt) neste ponto.
        // E que o custom_spinner_selected_item.xml tenha um TextView
        TextView tempSpinnerTextView = (TextView) spinnerCompanySize.getSelectedView();
        if (tempSpinnerTextView != null) {
            defaultSpinnerTextColor = tempSpinnerTextView.getCurrentTextColor();
        } else {
            // Cor de fallback se getSelectedView() for nulo inicialmente (raro, mas seguro)
            defaultSpinnerTextColor = Color.BLACK; // Ou a cor padrão do seu tema
        }


        spinnerCompanySize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelecionado = parent.getItemAtPosition(position).toString();
                String promptText = "";
                if (getResources().getStringArray(R.array.company_size_options).length > 0) {
                    promptText = getResources().getStringArray(R.array.company_size_options)[0];
                }

                // O 'view' passado aqui é o layout do item SELECIONADO (custom_spinner_selected_item)
                TextView selectedTextView = null;
                if (view instanceof TextView) {
                    selectedTextView = (TextView) view;
                }
                // Se o view não for um TextView diretamente (ex: se for um ViewGroup contendo o TextView),
                // você precisaria fazer: selectedTextView = view.findViewById(android.R.id.text1);

                if (position == 0 || itemSelecionado.equals(promptText)) {
                    selectedCompanySize = "";
                    if (selectedTextView != null) {
                        selectedTextView.setTextColor(Color.GRAY); // Define a cor para CINZA para o prompt
                    }
                } else {
                    selectedCompanySize = itemSelecionado;
                    if (selectedTextView != null) {
                        selectedTextView.setTextColor(defaultSpinnerTextColor); // Restaura a cor padrão
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCompanySize = "";
                // Se nada for selecionado, você pode querer garantir que o texto do spinner
                // (se ainda estiver visível e for o prompt) fique cinza.
                // Mas geralmente, o OnItemSelectedListener é chamado com a posição 0 na inicialização.
                View currentSelectedView = spinnerCompanySize.getSelectedView();
                if (currentSelectedView instanceof TextView) {
                    ((TextView) currentSelectedView).setTextColor(Color.GRAY);
                }
            }
        });

        // Chamar onItemSelected manualmente após definir o listener para aplicar a cor inicial
        // Isso garante que o prompt seja estilizado corretamente na primeira vez que a tela é carregada.
        if (spinnerCompanySize.getCount() > 0) {
            spinnerCompanySize.getOnItemSelectedListener().onItemSelected(spinnerCompanySize, spinnerCompanySize.getSelectedView(), 0, spinnerCompanySize.getItemIdAtPosition(0));
        }

    }

    public void onRegisterButtonClicked(View view) {
        // ... (seu código onRegisterButtonClicked permanece o mesmo)
        String promptText = "";
        if (getResources().getStringArray(R.array.company_size_options).length > 0) {
            promptText = getResources().getStringArray(R.array.company_size_options)[0];
        }

        if (selectedCompanySize.isEmpty() || selectedCompanySize.equals(promptText)) {
            Toast.makeText(this, "Por favor, selecione o tamanho da empresa.", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Registrando com tamanho: " + selectedCompanySize, Toast.LENGTH_SHORT).show();
    }
}
