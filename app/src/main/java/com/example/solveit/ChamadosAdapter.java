package com.example.solveit;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveit.api.ChamadoDTO;

import java.util.List;

public class ChamadosAdapter extends RecyclerView.Adapter<ChamadosAdapter.ChamadoViewHolder> {

    private final List<ChamadoDTO> chamadosList;
    private final Context context;
    private OnItemClickListener listener; // A interface para o clique

    // =========================================================================
    // ✨ MELHORIA: A interface agora passa o objeto DTO inteiro. ✨
    // Isso dá mais flexibilidade para a Activity, que não precisará buscar o
    // chamado novamente na lista.
    // =========================================================================
    public interface OnItemClickListener {
        void onItemClick(ChamadoDTO chamado); // Em vez de apenas o ID, passamos o objeto todo
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Construtor
    public ChamadosAdapter(Context context, List<ChamadoDTO> chamadosList) {
        this.context = context;
        this.chamadosList = chamadosList;
    }

    @NonNull
    @Override
    public ChamadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chamado_adm, parent, false);
        return new ChamadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChamadoViewHolder holder, int position) {
        ChamadoDTO chamado = chamadosList.get(position);

        holder.tvId.setText(String.valueOf(chamado.getId_chamado()));
        holder.tvTitulo.setText(chamado.getTitulo());
        holder.tvPrioridade.setText(chamado.getDesc_prioridade());
        holder.tvStatus.setText(chamado.getDesc_status());

        // Sua lógica de cor, que já está perfeita, continua aqui
        int corFundo;
        switch (chamado.getDesc_prioridade().toLowerCase()) {
            case "urgente":
                corFundo = ContextCompat.getColor(context, R.color.prioridade_urgente);
                break;
            case "alta":
                corFundo = ContextCompat.getColor(context, R.color.prioridade_alta);
                break;
            case "media":
            case "média":
                corFundo = ContextCompat.getColor(context, R.color.prioridade_media);
                break;
            case "baixa":
                corFundo = ContextCompat.getColor(context, R.color.prioridade_baixa);
                break;
            default:
                corFundo = ContextCompat.getColor(context, R.color.prioridade_default);
                break;
        }

        GradientDrawable background = (GradientDrawable) holder.tvPrioridade.getBackground();
        background.setColor(corFundo);
    }

    @Override
    public int getItemCount() {
        return chamadosList.size();
    }

    public void updateChamados(List<ChamadoDTO> novosChamados) {
        this.chamadosList.clear();
        this.chamadosList.addAll(novosChamados);
        notifyDataSetChanged();
    }

    // ViewHolder
    public class ChamadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvTitulo, tvPrioridade, tvStatus;

        public ChamadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_item_id);
            tvTitulo = itemView.findViewById(R.id.tv_item_titulo);
            tvPrioridade = itemView.findViewById(R.id.tv_item_prioridade);
            tvStatus = itemView.findViewById(R.id.tv_item_status);

            // =========================================================================
            // ✨ SEU CÓDIGO DE CLIQUE, AGORA USANDO A INTERFACE MELHORADA ✨
            // =========================================================================
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                // Verifica se o listener não é nulo e se a posição é válida
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    // Passa o objeto ChamadoDTO inteiro da posição clicada
                    listener.onItemClick(chamadosList.get(position));
                }
            });
        }
    }
}
