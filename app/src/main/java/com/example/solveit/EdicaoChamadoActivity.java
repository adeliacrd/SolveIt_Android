package com.example.solveit;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EdicaoChamadoActivity extends AppCompatActivity {

    private TextInputEditText etTitulo, etSolicitante, etEmail, etDescricao;
    private AutoCompleteTextView spinnerPrioridade;
    private MaterialButton btnConfirmar, btnCancelar;
    private ArrayAdapter<String> prioridadeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicao_chamado);

        // ✅ CORREÇÃO: Apenas configura a Toolbar, sem botão de voltar
        Toolbar toolbar = findViewById(R.id.toolbar_edicao_chamado);
        setSupportActionBar(toolbar);
        // Removemos toda a lógica de "setDisplayHomeAsUpEnabled" e "setNavigationOnClickListener"

        conectarComponentes();
        configurarSpinnerPrioridade();
        receberDadosDoChamado();

        btnConfirmar.setOnClickListener(v -> salvarAlteracoes());
        btnCancelar.setOnClickListener(v -> finish()); // O botão cancelar agora fecha a tela
    }

    private void conectarComponentes() {
        etTitulo = findViewById(R.id.et_edicao_titulo);
        etSolicitante = findViewById(R.id.et_edicao_solicitante);
        etEmail = findViewById(R.id.et_edicao_email);
        etDescricao = findViewById(R.id.et_edicao_descricao);
        spinnerPrioridade = findViewById(R.id.spinner_edicao_prioridade);
        btnConfirmar = findViewById(R.id.btn_edicao_confirmar);
        btnCancelar = findViewById(R.id.btn_edicao_cancelar);
    }

    private void configurarSpinnerPrioridade() {
        String[] prioridades = new String[]{"Urgente", "Alta", "Média", "Baixa"};
        prioridadeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, prioridades);
        spinnerPrioridade.setAdapter(prioridadeAdapter);
    }

    private void receberDadosDoChamado() {
        String titulo = getIntent().getStringExtra("CHAMADO_TITULO");
        String solicitante = getIntent().getStringExtra("CHAMADO_SOLICITANTE");
        String prioridadeAtual = "Baixa";

        etTitulo.setText(titulo);
        etSolicitante.setText(solicitante);
        etEmail.setText("nome.usuario@empresa.com");
        etDescricao.setText("A impressora da sala de reuniões simplesmente parou de funcionar. Já tentei reiniciar e nada acontece.");

        if (prioridadeAtual != null) {
            spinnerPrioridade.setText(prioridadeAtual, false);
        }
    }

    private void salvarAlteracoes() {
        String novoTitulo = etTitulo.getText().toString();
        String novaPrioridade = spinnerPrioridade.getText().toString();

        Toast.makeText(this, "Alterações salvas com sucesso (simulação)!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
