package com.example.solveit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveit.api.ChamadoDTO;

import java.util.List;

public class ChamadosAdapter extends RecyclerView.Adapter<ChamadosAdapter.ChamadoViewHolder> {

    private List<ChamadoDTO> chamadosList;
    private Context context;
    private OnItemClickListener listener;
    private int idUsuarioLogado;

    public interface OnItemClickListener {
        void onItemClick(int idChamado);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ChamadosAdapter(Context context, List<ChamadoDTO> chamadosList, int idUsuarioLogado) {
        this.context = context;
        this.chamadosList = chamadosList;
        this.idUsuarioLogado = idUsuarioLogado;
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

        // --- 1. PREENCHIMENTO DOS DADOS DO CABEÇALHO ---
        holder.tvId.setText(String.valueOf(chamado.getId_chamado()));
        holder.tvTitulo.setText(chamado.getTitulo());
        holder.tvPrioridade.setText(chamado.getDesc_prioridade());
        holder.tvStatus.setText(chamado.getDesc_status());

        // --- 2. PREENCHIMENTO DOS DADOS DA EXPANSÃO ---
        holder.tvExpandidoTitulo.setText(chamado.getTitulo());

        String nomeSolicitante = (chamado.getNome_solicitante() != null) ? chamado.getNome_solicitante() : "Desconhecido";
        holder.tvExpandidoSolicitante.setText("Solicitante: " + nomeSolicitante);

        holder.tvExpandidoDescricao.setText(chamado.getDesc_chamado());

        // ✨ NOVO: Preenche o tempo (Placeholder por enquanto, ou cálculo de data)
        holder.tvExpandidoTempo.setText("0d 00h 00m");

        // --- 3. LÓGICA DE VISIBILIDADE (EXPANDIR/CONTRAIR) ---
        boolean isExpanded = chamado.isExpanded();
        holder.layoutDetalhes.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // --- 4. LÓGICA VISUAL COMPLEXA (CORES E BORDAS) ---
        String status = chamado.getDesc_status().toLowerCase();
        boolean isConcluido = status.contains("concluído") || status.contains("concluido");
        boolean temAvaliacao = chamado.getNota_avaliacao() != null && !chamado.getNota_avaliacao().isEmpty();

        // A. Cor do Texto do Status (Sempre Cinza na lista, conforme seu pedido)
        holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.solveit_texto_secundario));

        // B. Definição das Cores de Fundo e Prioridade
        int corPrioridade;
        int corFundoLinha;

        if (isConcluido) {
            // Prioridade sempre cinza se acabou
            corPrioridade = ContextCompat.getColor(context, R.color.prioridade_default); // ou prioridade_default

            if (temAvaliacao) {
                corFundoLinha = Color.parseColor("#F5F5F5"); // Cinza (Item morto)
            } else {
                corFundoLinha = Color.WHITE; // Branco (Destaque para avaliar)
            }
        } else {
            // Aberto/Andamento

            // Cor do Fundo (Azul se for meu, Branco se for de outro)
            if (chamado.getId_usuario() == idUsuarioLogado) {
                corFundoLinha = Color.parseColor("#E3F2FD");
            } else {
                corFundoLinha = Color.WHITE;
            }

            // Cor da Prioridade (Semáforo)
            switch (chamado.getDesc_prioridade().toLowerCase()) {
                case "urgente": corPrioridade = ContextCompat.getColor(context, R.color.prioridade_urgente); break;
                case "alta": corPrioridade = ContextCompat.getColor(context, R.color.prioridade_alta); break;
                case "média": case "media": corPrioridade = ContextCompat.getColor(context, R.color.prioridade_media); break;
                case "baixa": corPrioridade = ContextCompat.getColor(context, R.color.prioridade_baixa); break;
                default: corPrioridade = ContextCompat.getColor(context, R.color.prioridade_default); break;
            }
        }

        // C. Aplicação Visual (Fundo e Borda)
        GradientDrawable bgLinha = new GradientDrawable();
        bgLinha.setShape(GradientDrawable.RECTANGLE);
        int FundoExpandido = Color.parseColor("#E4EBF4");

        if (isExpanded) {
            // ✨ EXPANDIDO: Apenas fundo branco, SEM borda azul ✨
            bgLinha.setColor(FundoExpandido);
        } else {
            // ✨ FECHADO: Fundo colorido (Cinza/Azul) ✨
            bgLinha.setColor(corFundoLinha);
        }

        // Aplica o fundo na linha (Cabeçalho)
        holder.layoutCabecalho.setBackground(bgLinha);

        // Aplica a cor na etiqueta de prioridade
        GradientDrawable bgPrioridade = (GradientDrawable) holder.tvPrioridade.getBackground();
        bgPrioridade.setColor(corPrioridade);

        // --- 5. CLIQUES ---
        holder.layoutCabecalho.setOnClickListener(v -> {
            chamado.setExpanded(!chamado.isExpanded());
            notifyItemChanged(position);
        });

        holder.btnIrParaChamado.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(chamado.getId_chamado());
            }
        });
    }

    @Override
    public int getItemCount() { return chamadosList != null ? chamadosList.size() : 0; }

    public void updateChamados(List<ChamadoDTO> novosChamados) {
        this.chamadosList.clear();
        this.chamadosList.addAll(novosChamados);
        notifyDataSetChanged();
    }

    // --- VIEWHOLDER ---
    public class ChamadoViewHolder extends RecyclerView.ViewHolder {
        // Cabeçalho
        LinearLayout layoutCabecalho;
        TextView tvId, tvTitulo, tvPrioridade, tvStatus;

        // Expansão
        LinearLayout layoutDetalhes;
        TextView tvExpandidoTitulo, tvExpandidoSolicitante, tvExpandidoDescricao, tvExpandidoTempo; // ✨ Adicionado
        Button btnIrParaChamado, btnEditarChamado;

        public ChamadoViewHolder(@NonNull View itemView) {
            super(itemView);
            // IDs do Cabeçalho
            layoutCabecalho = itemView.findViewById(R.id.layout_cabecalho);
            tvId = itemView.findViewById(R.id.tv_item_id);
            tvTitulo = itemView.findViewById(R.id.tv_item_titulo);
            tvPrioridade = itemView.findViewById(R.id.tv_item_prioridade);
            tvStatus = itemView.findViewById(R.id.tv_item_status);

            // IDs da Expansão
            layoutDetalhes = itemView.findViewById(R.id.layout_detalhes_expandidos);
            tvExpandidoTitulo = itemView.findViewById(R.id.tv_expandido_titulo);
            tvExpandidoSolicitante = itemView.findViewById(R.id.tv_expandido_solicitante);
            tvExpandidoDescricao = itemView.findViewById(R.id.tv_expandido_descricao);
            tvExpandidoTempo = itemView.findViewById(R.id.tv_expandido_tempo); // ✨ Adicionado
            btnIrParaChamado = itemView.findViewById(R.id.btn_ir_para_chamado);
            //btnEditarChamado = itemView.findViewById(R.id.btn_editar_chamado);
        }
    }
}