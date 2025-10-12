package com.example.solveit;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

public class ChamadosAdapter extends RecyclerView.Adapter<ChamadosAdapter.ChamadoViewHolder> {

    private List<Chamado> listaChamados;

    // Construtor do Adapter
    public ChamadosAdapter(List<Chamado> listaChamados) {
        this.listaChamados = listaChamados;
    }

    // 1. Este método cria o layout do item (a linha) pela primeira vez
    @NonNull
    @Override
    public ChamadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chamado_layout, parent, false);
        return new ChamadoViewHolder(itemView);
    }

    // 2. Este método preenche cada linha com os dados corretos (título, status, etc.)
    @Override
    public void onBindViewHolder(@NonNull ChamadoViewHolder holder, int position) {
        Chamado chamadoAtual = listaChamados.get(position);

        // Preenche as Views com os dados do chamado
        holder.textViewId.setText(String.valueOf(chamadoAtual.getId()));
        holder.textViewTitulo.setText(chamadoAtual.getTitulo());
        holder.chipPrioridade.setText(chamadoAtual.getPrioridade());
        holder.textViewStatus.setText(chamadoAtual.getStatus());

        // Lógica para definir a cor do Chip de prioridade
        switch (chamadoAtual.getPrioridade().toLowerCase()) {
            case "urgente":
                holder.chipPrioridade.setChipBackgroundColorResource(R.color.prioridade_urgente);
                break;
            case "alta":
                holder.chipPrioridade.setChipBackgroundColorResource(R.color.prioridade_alta);
                break;
            case "media":
                holder.chipPrioridade.setChipBackgroundColorResource(R.color.prioridade_media);
                break;
            case "baixa":
                holder.chipPrioridade.setChipBackgroundColorResource(R.color.prioridade_baixa);
                break;
            default:
                // Uma cor padrão caso a prioridade não seja nenhuma das esperadas
                holder.chipPrioridade.setChipBackgroundColorResource(R.color.solveit_texto_secundario);
                break;
        }
    }

    // 3. Este método diz à lista quantos itens ela tem no total
    @Override
    public int getItemCount() {
        // Garante que a lista nunca seja nula, evitando crashes
        return listaChamados != null ? listaChamados.size() : 0;
    }

    // =============================================================
    // ✨ MÉTODO NOVO QUE CORRIGE O ERRO DE COMPILAÇÃO ✨
    // =============================================================
    /**
     * Atualiza a lista de chamados exibida pelo adapter.
     * @param novaLista A nova lista de chamados a ser exibida.
     */
    public void atualizarLista(List<Chamado> novaLista) {
        // Limpa a lista antiga
        this.listaChamados.clear();
        // Adiciona todos os itens da nova lista
        this.listaChamados.addAll(novaLista);
        // Notifica o RecyclerView que os dados mudaram, para que ele se redesenhe
        notifyDataSetChanged();
    }
    // =============================================================

    // 4. Esta classe interna representa os componentes visuais de CADA linha da lista
    public static class ChamadoViewHolder extends RecyclerView.ViewHolder {

        // Declaração dos componentes visuais do layout 'item_chamado_layout.xml'
        TextView textViewId;
        TextView textViewTitulo;
        Chip chipPrioridade;
        TextView textViewStatus;

        public ChamadoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Conecta as variáveis acima com os componentes reais do XML pelo ID deles
            textViewId = itemView.findViewById(R.id.textViewId);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            chipPrioridade = itemView.findViewById(R.id.chipPrioridade);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
