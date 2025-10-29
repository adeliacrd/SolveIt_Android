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
import java.util.List;

public class ChamadosAdminAdapter extends RecyclerView.Adapter<ChamadosAdminAdapter.ChamadoAdminViewHolder> {

    private final List<Chamado> chamados;
    private final Context context;

    public ChamadosAdminAdapter(Context context, List<Chamado> chamados) {
        this.context = context;
        this.chamados = chamados;
    }

    @NonNull
    @Override
    public ChamadoAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chamado_admin, parent, false);
        return new ChamadoAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChamadoAdminViewHolder holder, int position) {
        Chamado chamado = chamados.get(position);
        holder.bind(chamado);
    }

    @Override
    public int getItemCount() {
        return chamados.size();
    }

    class ChamadoAdminViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId, tvTitulo, tvPrioridade, tvStatus;

        public ChamadoAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.textViewAdminChamadoId);
            tvTitulo = itemView.findViewById(R.id.textViewAdminChamadoTitulo);
            tvPrioridade = itemView.findViewById(R.id.textViewAdminChamadoPrioridade);
            tvStatus = itemView.findViewById(R.id.textViewAdminChamadoStatus);
        }

        public void bind(Chamado chamado) {
            tvId.setText(String.valueOf(chamado.getId()));
            tvTitulo.setText(chamado.getTitulo());
            tvPrioridade.setText(chamado.getPrioridade());
            tvStatus.setText(chamado.getStatus());

            // Lógica para colorir a tag de prioridade
            int colorResId;
            switch (chamado.getPrioridade().toLowerCase()) {
                case "urgente":
                    colorResId = R.color.prioridade_urgente;
                    break;
                case "alta":
                    colorResId = R.color.prioridade_alta;
                    break;
                case "media": // Note: a imagem usa 'Média' com acento, mas é melhor padronizar
                case "média":
                    colorResId = R.color.prioridade_media;
                    break;
                case "baixa":
                    colorResId = R.color.prioridade_baixa;
                    break;
                default:
                    colorResId = android.R.color.darker_gray;
                    break;
            }
            GradientDrawable background = (GradientDrawable) tvPrioridade.getBackground();
            background.setColor(ContextCompat.getColor(context, colorResId));
        }
    }
}
