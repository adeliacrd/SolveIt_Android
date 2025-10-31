package com.example.solveit;

import android.content.Context;
import android.graphics.Color; // Importe a classe Color
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Importe ContextCompat
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveit.api.ChamadoDTO; // Importe seu ChamadoDTO

import java.util.List;
import android.graphics.drawable.GradientDrawable;

public class ChamadosAdapter extends RecyclerView.Adapter<ChamadosAdapter.ChamadoViewHolder> {

    private List<ChamadoDTO> chamadosList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int idChamado);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Construtor (já estava correto)
    public ChamadosAdapter(Context context, List<ChamadoDTO> chamadosList) {
        this.context = context;
        this.chamadosList = chamadosList;
    }

    @NonNull
    @Override
    public ChamadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✨ CORREÇÃO AQUI: Infla o layout correto do ADM ✨
        View view = LayoutInflater.from(context).inflate(R.layout.item_chamado_adm, parent, false);
        return new ChamadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChamadoViewHolder holder, int position) {
        ChamadoDTO chamado = chamadosList.get(position);

        // Preenche os TextViews com os dados do chamado
        // (A linha 55, que quebrava, era uma destas)
        holder.tvId.setText(String.valueOf(chamado.getId_chamado()));
        holder.tvTitulo.setText(chamado.getTitulo());
        holder.tvPrioridade.setText(chamado.getDesc_prioridade());
        holder.tvStatus.setText(chamado.getDesc_status());

        // Lógica de Cor (já estava correta)
        int corFundo;
        switch (chamado.getDesc_prioridade().toLowerCase()) { // Usando toLowerCase() por segurança
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

        // 2. Pega o "molde" (bg_rounded_tag.xml) do TextView
        // Precisamos importar android.graphics.drawable.GradientDrawable
        GradientDrawable background = (GradientDrawable) holder.tvPrioridade.getBackground();

        // 3. Pinta o "molde" com a cor correta
        background.setColor(corFundo);
    }

    @Override
    public int getItemCount() {
        return chamadosList.size();
    }

    // Método para atualizar a lista (já estava correto)
    public void updateChamados(List<ChamadoDTO> novosChamados) {
        this.chamadosList.clear();
        this.chamadosList.addAll(novosChamados);
        notifyDataSetChanged();
    }

    // ViewHolder que "segura" as Views de cada linha
    public class ChamadoViewHolder extends RecyclerView.ViewHolder {
        // ✨ Garante que os nomes das variáveis batem com os IDs do XML ✨
        TextView tvId, tvTitulo, tvPrioridade, tvStatus;

        public ChamadoViewHolder(@NonNull View itemView) {
            super(itemView);
            // ✨ Garante que os IDs R.id.* batem com o arquivo item_chamado_adm.xml ✨
            tvId = itemView.findViewById(R.id.tv_item_id);
            tvTitulo = itemView.findViewById(R.id.tv_item_titulo);
            tvPrioridade = itemView.findViewById(R.id.tv_item_prioridade);
            tvStatus = itemView.findViewById(R.id.tv_item_status);

            // Configura o clique no item da lista
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(chamadosList.get(getAdapterPosition()).getId_chamado());
                }
            });
        }
    }
}
