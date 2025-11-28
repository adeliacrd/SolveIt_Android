package com.example.solveit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EdicaoChamadoFragment extends Fragment {

    // Todos os componentes que você já tinha
    private TextInputEditText etTitulo, etSolicitante, etEmail, etDescricao;
    private AutoCompleteTextView spinnerPrioridade;
    private MaterialButton btnConfirmar, btnCancelar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout do fragmento
        return inflater.inflate(R.layout.fragment_edicao_chamado, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // A lógica do seu antigo onCreate() vem para cá
        conectarComponentes(view);
        configurarSpinnerPrioridade();
        receberDadosDoChamado(); // Podemos ajustar isso depois

        btnConfirmar.setOnClickListener(v -> salvarAlteracoes());
        btnCancelar.setOnClickListener(v -> {
            // Em um fragmento, para fechar a tela, pedimos para a Activity fazer isso
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    // A lógica para encontrar os componentes agora precisa da "view" do fragmento
    private void conectarComponentes(View view) {
        etTitulo = view.findViewById(R.id.et_edicao_titulo);
        etSolicitante = view.findViewById(R.id.et_edicao_solicitante);
        etEmail = view.findViewById(R.id.et_edicao_email);
        etDescricao = view.findViewById(R.id.et_edicao_descricao);
        spinnerPrioridade = view.findViewById(R.id.spinner_edicao_prioridade);
        btnConfirmar = view.findViewById(R.id.btn_edicao_confirmar);
        btnCancelar = view.findViewById(R.id.btn_edicao_cancelar);
    }

    private void configurarSpinnerPrioridade() {
        String[] prioridades = new String[]{"Urgente", "Alta", "Média", "Baixa"};
        // O contexto agora é pego do fragmento usando "requireContext()"
        ArrayAdapter<String> prioridadeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, prioridades);
        spinnerPrioridade.setAdapter(prioridadeAdapter);
    }

    private void receberDadosDoChamado() {
        // Vamos deixar os dados fixos por enquanto para simplificar
        // Depois vamos pegar os dados da Activity que hospeda o fragmento
        etTitulo.setText("Impressora não funciona na sala de reuniões");
        etSolicitante.setText("Nome do Usuário");
        etEmail.setText("nome.usuario@empresa.com");
        etDescricao.setText("A impressora da sala de reuniões simplesmente parou de funcionar. Já tentei reiniciar e nada acontece.");
        spinnerPrioridade.setText("Alta", false);
    }

    private void salvarAlteracoes() {
        Toast.makeText(getContext(), "Alterações salvas (simulação)!", Toast.LENGTH_SHORT).show();
    }
}
