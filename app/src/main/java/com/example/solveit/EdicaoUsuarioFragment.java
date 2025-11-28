package com.example.solveit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class EdicaoUsuarioFragment extends Fragment {

    // Componentes da tela
    private RecyclerView rvUsuarios;
    private TextInputEditText etPesquisar;
    private Button btnConfirmar, btnCancelar;
    private AutoCompleteTextView spinnerFiltro;

    // Adaptador e lista de dados
    private UsuarioAdapter adapter;
    private List<Usuario> listaDeUsuarios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edicao_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Conectar os componentes do layout
        rvUsuarios = view.findViewById(R.id.rv_usuarios);
        etPesquisar = view.findViewById(R.id.et_pesquisar_usuario);
        btnConfirmar = view.findViewById(R.id.btn_confirmar_edicao);
        btnCancelar = view.findViewById(R.id.btn_cancelar_edicao);
        spinnerFiltro = view.findViewById(R.id.spinner_filtro_status);

        // 2. Criar os dados de exemplo
        criarDadosDeExemplo();

        // 3. Configurar o RecyclerView
        rvUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));

        // 4. Criar e configurar o Adapter principal
        adapter = new UsuarioAdapter(listaDeUsuarios);
        rvUsuarios.setAdapter(adapter);

        // 5. Configurar o dropdown (Spinner)
        configurarFiltroStatus();

        // 6. Configurar a barra de pesquisa
        etPesquisar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        // 7. Configurar os cliques dos botões
        btnConfirmar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Alterações salvas!", Toast.LENGTH_SHORT).show();
        });

        btnCancelar.setOnClickListener(v -> {
            // Fecha a activity inteira, uma ação mais comum para "Cancelar"
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    // Método para configurar o dropdown
    private void configurarFiltroStatus() {
        // Cria a lista de opções
        String[] opcoes = new String[]{"Todos", "Ativo", "Inativo"};

        // Cria um adaptador simples para o dropdown
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, opcoes);

        // Define o adaptador para o nosso AutoCompleteTextView
        spinnerFiltro.setAdapter(arrayAdapter);

        // Define a ação a ser executada quando um item é selecionado
        spinnerFiltro.setOnItemClickListener((parent, view, position, id) -> {
            // Pega o texto da opção selecionada (ex: "Ativo")
            String itemSelecionado = (String) parent.getItemAtPosition(position);

            // Chama o novo método de filtro no nosso adaptador
            adapter.filtrarPorStatus(itemSelecionado);
        });
    }

    /**
     * ✅ CORREÇÃO APLICADA AQUI
     * Este método agora inicializa a lista e a preenche com dados de exemplo,
     * resolvendo o NullPointerException.
     */
    private void criarDadosDeExemplo() {
        // Inicializa a lista antes de adicionar itens a ela
        listaDeUsuarios = new ArrayList<>();

        // Adiciona os usuários de exemplo
        listaDeUsuarios.add(new Usuario("Adelia Cardoso", "adelia.cardoso@empresa.com", "11 98888-1111", "Empresa 1", true));
        listaDeUsuarios.add(new Usuario("Bruno Agente", "bruno.agente@empresa.com", "21 97777-2222", "Empresa 2", false));
        listaDeUsuarios.add(new Usuario("Carla Silva", "carla.s@empresa.com", "31 96666-3333", "Empresa 1", true));
        listaDeUsuarios.add(new Usuario("Daniel Souza", "d.souza@empresa.com", "41 95555-4444", "Empresa 3", false));
    }
}
