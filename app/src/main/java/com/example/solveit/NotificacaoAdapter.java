package com.example.solveit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Usando o nome de classe NotificacaoAdapter e o modelo Notificacao
public class NotificacaoAdapter extends RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder> {

    private List<Notificacao> listaNotificacoes;

    // Construtor: Recebe a lista de dados
    public NotificacaoAdapter(List<Notificacao> listaNotificacoes) {
        this.listaNotificacoes = listaNotificacoes;
    }

    // O ViewHolder contém a referência para as views do item (o item_notificacao.xml)
    public static class NotificacaoViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMensagem;
        public TextView textViewDataId;

        public NotificacaoViewHolder(View itemView) {
            super(itemView);
            // Referencia os IDs do item_notificacao.xml
            textViewMensagem = itemView.findViewById(R.id.text_view_mensagem);
            textViewDataId = itemView.findViewById(R.id.text_view_data_id);
        }
    }

    @NonNull
    @Override
    // Cria novas ViewHolders (infla o item_notificacao.xml)
    public NotificacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacao, parent, false);
        return new NotificacaoViewHolder(view);
    }

    @Override
    // Liga os dados do Notificacao à View
    public void onBindViewHolder(@NonNull NotificacaoViewHolder holder, int position) {
        Notificacao notificacao = listaNotificacoes.get(position);

        holder.textViewMensagem.setText(notificacao.getMensagem());
        holder.textViewDataId.setText(notificacao.getDataHoraId());

        // Adiciona um listener para o clique em cada notificação
        holder.itemView.setOnClickListener(v -> {
            // Lógica para extrair o ID e mostrar um Toast (pode ser substituído por uma Intent para outra Activity)
            String chamadoId = notificacao.getDataHoraId().substring(notificacao.getDataHoraId().lastIndexOf(" - ") + 3);
            Toast.makeText(v.getContext(), "Abrir detalhes do " + chamadoId, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    // Retorna o número total de itens na lista
    public int getItemCount() {
        return listaNotificacoes.size();
    }
}
