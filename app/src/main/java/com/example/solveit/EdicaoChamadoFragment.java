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

    // 1. DECLARAÇÃO DAS VARIÁVEIS DO CHAMADO
    private TextInputEditText etTitulo, etSolicitante, etEmail, etDescricao;
    private AutoCompleteTextView spinnerPrioridade;
    private AutoCompleteTextView spinnerAtribuirAgente;
    private MaterialButton btnConfirmar, btnCancelar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ✅ AQUI ESTÁ A LIGAÇÃO CORRETA!
        // Este fragmento Java deve usar o layout de EDIÇÃO DE CHAMADO.
        return inflater.inflate(R.layout.fragment_edicao_chamado, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // O resto do código funciona se a ligação acima estiver certa.
        conectarComponentes(view);
        configurarSpinners();
        receberDadosDoChamado();
        configurarBotoes();
    }

    private void conectarComponentes(View view) {
        // Estes IDs existem no 'fragment_edicao_chamado.xml'
        etTitulo = view.findViewById(R.id.et_titulo_chamado);
        etSolicitante = view.findViewById(R.id.et_solicitante_chamado);
        etEmail = view.findViewById(R.id.et_email_chamado);
        etDescricao = view.findViewById(R.id.et_descricao_chamado);
        spinnerPrioridade = view.findViewById(R.id.spinner_prioridade);
        spinnerAtribuirAgente = view.findViewById(R.id.spinner_atribuir_agente);
        btnConfirmar = view.findViewById(R.id.btn_confirmar_edicao);
        btnCancelar = view.findViewById(R.id.btn_cancelar_edicao);
    }

    // O resto do código...
    private void configurarSpinners() {
        String[] prioridades = new String[]{"Urgente", "Alta", "Média", "Baixa"};
        ArrayAdapter<String> prioridadeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, prioridades);
        spinnerPrioridade.setAdapter(prioridadeAdapter);

        String[] agentes = new String[]{"Agente Suporte N1", "Bruno Agente", "Carla Silva", "Não atribuído"};
        ArrayAdapter<String> agenteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, agentes);
        spinnerAtribuirAgente.setAdapter(agenteAdapter);
    }

    private void configurarBotoes() {
        btnConfirmar.setOnClickListener(v -> salvarAlteracoes());
        btnCancelar.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void receberDadosDoChamado() {
        etTitulo.setText("Impressora não funciona na sala de reuniões");
        etSolicitante.setText("Nome do Usuário");
        etEmail.setText("nome.usuario@empresa.com");
        etDescricao.setText("A impressora da sala de reuniões simplesmente parou de funcionar.");
        spinnerPrioridade.setText("Alta", false);
        spinnerAtribuirAgente.setText("Não atribuído", false);
    }

    private void salvarAlteracoes() {
        String prioridade = spinnerPrioridade.getText().toString();
        String agente = spinnerAtribuirAgente.getText().toString();
        String mensagem = "Prioridade: " + prioridade + "\nAtribuído a: " + agente;
        Toast.makeText(getContext(), "Alterações salvas!\n" + mensagem, Toast.LENGTH_LONG).show();
    }
}
