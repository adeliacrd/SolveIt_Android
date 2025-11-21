package com.example.solveit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MensagemViewHolder> {

    private List<Mensagem> listaDeMensagens;

    // Construtor que recebe a lista de dados
    public MensagensAdapter(List<Mensagem> listaDeMensagens) {
        this.listaDeMensagens = listaDeMensagens;
    }

    // 1. Cria a "caixa" visual para um item da lista
    @NonNull
    @Override
    public MensagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensagem, parent, false);
        return new MensagemViewHolder(view);
    }

    // 2. Pega os dados de uma posição e os coloca na "caixa" visual
    @Override
    public void onBindViewHolder(@NonNull MensagemViewHolder holder, int position) {
        Mensagem mensagemAtual = listaDeMensagens.get(position);
        holder.bind(mensagemAtual);
    }

    // 3. Retorna a quantidade total de itens na lista
    @Override
    public int getItemCount() {
        return listaDeMensagens.size();
    }

    // Classe interna que representa a "caixa" visual (os TextViews de um item)
    class MensagemViewHolder extends RecyclerView.ViewHolder {
        TextView tvIniciais, tvNomeUsuario, tvTipo, tvData, tvTexto;

        public MensagemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIniciais = itemView.findViewById(R.id.tv_mensagem_iniciais);
            tvNomeUsuario = itemView.findViewById(R.id.tv_mensagem_nome_usuario);
            tvTipo = itemView.findViewById(R.id.tv_mensagem_tipo);
            tvData = itemView.findViewById(R.id.tv_mensagem_data);
            tvTexto = itemView.findViewById(R.id.tv_mensagem_texto);
        }

        // Método que preenche os componentes visuais com os dados da mensagem
        void bind(Mensagem mensagem) {
            tvIniciais.setText(mensagem.getIniciais());
            tvNomeUsuario.setText(mensagem.getNomeUsuario());
            tvTipo.setText(mensagem.getTipo());
            tvData.setText(mensagem.getData());
            tvTexto.setText(mensagem.getTexto());
        }
    }
}
    