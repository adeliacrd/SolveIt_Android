package com.example.solveit;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Usando o nome de classe NotificacoesActivity
public class NotificacoesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textViewNenhumaNotificacao;
    // Usando NotificacaoAdapter e Notificacao, conforme a estrutura do seu projeto
    private NotificacaoAdapter adapter;
    private List<Notificacao> listaNotificacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usando o layout activity_notificacoes.xml
        setContentView(R.layout.activity_notificacoes);

        // 1. Configuração da Barra Superior e Botão Voltar (Seta)
        ImageButton btnVoltarTopo = findViewById(R.id.btn_voltar_notificacoes);
        btnVoltarTopo.setOnClickListener(v -> finish()); // Volta para a tela anterior

        // 2. Inicialização dos componentes da lista
        recyclerView = findViewById(R.id.recycler_view_notificacoes);
        textViewNenhumaNotificacao = findViewById(R.id.text_view_nenhuma_notificacao);

        // Define o LayoutManager (lista vertical)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Inicializa e popula a lista de dados
        listaNotificacoes = new ArrayList<>();
        popularListaNotificacoes(); // Preenche com os dados de exemplo

        // 4. Inicializa e atribui o Adapter ao RecyclerView
        adapter = new NotificacaoAdapter(listaNotificacoes);
        recyclerView.setAdapter(adapter);

        // 5. Lógica de exibição da mensagem de lista vazia
        if (listaNotificacoes.isEmpty()) {
            textViewNenhumaNotificacao.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            // Mensagem Toast útil para teste
            Toast.makeText(this, "Lista de notificações vazia", Toast.LENGTH_LONG).show();
        } else {
            textViewNenhumaNotificacao.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Exemplo de como configurar outros botões da Toolbar (Opcional)
        ImageButton actionAdd = findViewById(R.id.action_add);
        actionAdd.setOnClickListener(v -> Toast.makeText(this, "Ação Adicionar Clicada", Toast.LENGTH_SHORT).show());

        ImageButton actionProfile = findViewById(R.id.action_profile);
        actionProfile.setOnClickListener(v -> Toast.makeText(this, "Ação Perfil Clicada", Toast.LENGTH_SHORT).show());
    }

    /**
     * Método que simula a obtenção de dados (mockup) para preencher a lista.
     * Em um aplicativo real, este método faria uma chamada a um banco de dados ou API.
     */
    private void popularListaNotificacoes() {
        // Mockup com os dados da imagem (do mais novo para o mais antigo)
        // Usando o construtor da classe Notificacao
        listaNotificacoes.add(new Notificacao(
                "NomeAgente abriu o chamado",
                "29/04/2025 às 14h28 - Chamado ID3"
        ));
        listaNotificacoes.add(new Notificacao(
                "NomeAgente respondeu o chamado",
                "29/04/2025 às 14h20 - Chamado ID2"
        ));
        listaNotificacoes.add(new Notificacao(
                "NomeAgente abriu o chamado",
                "29/04/2025 às 14h28 - Chamado ID1"
        ));
    }
}
