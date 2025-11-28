package com.example.solveit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class EdicaoUsuarioFragment extends Fragment {

    private RecyclerView rvUsuarios;
    private UsuarioAdapter usuarioAdapter;
    private TextInputEditText etPesquisarUsuario;
    private AutoCompleteTextView spinnerFiltroStatus;
    private MaterialButton btnConfirmar, btnCancelar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edicao_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        conectarComponentes(view);
        configurarFiltros(); // Este método será modificado
        configurarPesquisa(); // Bônus: Filtro de pesquisa por texto
        configurarRecyclerView();
        configurarBotoes();

        carregarDadosDeExemplo(); // Este método também será modificado
    }

    private void conectarComponentes(View view) {
        rvUsuarios = view.findViewById(R.id.rv_usuarios);
        etPesquisarUsuario = view.findViewById(R.id.et_pesquisar_usuario);
        spinnerFiltroStatus = view.findViewById(R.id.spinner_filtro_status);
        btnConfirmar = view.findViewById(R.id.btn_confirmar_edicao);
        btnCancelar = view.findViewById(R.id.btn_cancelar_edicao);
    }

    // ✅ =====================================================================
    // ✅ MÉTODO `configurarFiltros` ATUALIZADO
    // ✅ =====================================================================
    private void configurarFiltros() {
        String[] status = new String[]{"Ativos", "Inativos", "Todos"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, status);
        spinnerFiltroStatus.setAdapter(statusAdapter);

        // Define "Ativos" como valor inicial, mas sem disparar o clique
        spinnerFiltroStatus.setText(status[0], false);

        // Adiciona um "ouvinte" que é acionado quando um item do dropdown é selecionado
        spinnerFiltroStatus.setOnItemClickListener((parent, view, position, id) -> {
            // Pega o texto do item que foi selecionado (ex: "Ativos", "Inativos")
            String itemSelecionado = (String) parent.getItemAtPosition(position);

            // Chama o método de filtro no adapter, passando a opção escolhida
            if (usuarioAdapter != null) {
                usuarioAdapter.filtrarPorStatus(itemSelecionado);
            }
        });
    }

    // Método bônus para fazer a barra de pesquisa funcionar em tempo real
    private void configurarPesquisa() {
        etPesquisarUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Sempre que o texto na barra de pesquisa mudar, chama o filtro do adapter
                if (usuarioAdapter != null) {
                    usuarioAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void configurarRecyclerView() {
        rvUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        usuarioAdapter = new UsuarioAdapter(new ArrayList<>());
        rvUsuarios.setAdapter(usuarioAdapter);
    }

    private void configurarBotoes() {
        btnConfirmar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Alterações de usuário salvas (simulação)!", Toast.LENGTH_SHORT).show();
        });

        btnCancelar.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    // ✅ =====================================================================
    // ✅ MÉTODO `carregarDadosDeExemplo` ATUALIZADO
    // ✅ =====================================================================
    private void carregarDadosDeExemplo() {
        List<Usuario> listaDeUsuarios = new ArrayList<>();
        listaDeUsuarios.add(new Usuario("Agente Suporte N1", "agente.n1@empresa.com", "11987654321", "SolveIT", true, "Agente"));
        listaDeUsuarios.add(new Usuario("Bruno Agente", "bruno.agente@empresa.com", "11912345678", "SolveIT", true, "Agente"));
        listaDeUsuarios.add(new Usuario("Carla Silva", "carla.silva@empresa.com", "21988887777", "Empresa Cliente A", false, "Cliente"));
        listaDeUsuarios.add(new Usuario("Daniel Martins", "daniel.m@empresa.com", "31999998888", "Empresa Cliente B", true, "Cliente"));

        // Atualiza a lista no adapter com os dados completos
        usuarioAdapter.atualizarLista(listaDeUsuarios);

        // Aplica o filtro inicial usando o texto que já está no spinner ("Ativos")
        // Isso garante que a tela já comece com a lista filtrada.
        usuarioAdapter.filtrarPorStatus(spinnerFiltroStatus.getText().toString());
    }
}
