package com.example.solveit;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Importar
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView; // Importar
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveit.api.InteracaoDTO;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private Context context;
    private List<InteracaoDTO> listaInteracoes;
    private int idUsuarioLogado;

    public TimelineAdapter(Context context, List<InteracaoDTO> listaInteracoes, int idUsuarioLogado) {
        this.context = context;
        this.listaInteracoes = listaInteracoes;
        this.idUsuarioLogado = idUsuarioLogado;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_interacao, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        InteracaoDTO interacao = listaInteracoes.get(position);

        // 1. Dados básicos
        String cabecalho = interacao.getNome_usuario() + " | " + interacao.getDt_interacao();
        holder.tvAutorData.setText(cabecalho);
        holder.tvMensagem.setText(interacao.getMensagem());

        // 2. Iniciais
        String iniciais = getIniciais(interacao.getNome_usuario());
        holder.tvAvatarInitials.setText(iniciais);
        holder.tvAvatarInitials.setVisibility(View.VISIBLE);
        holder.imgAvatarPhoto.setVisibility(View.GONE);

        // 3. Alinhamento e Cores
        // ✨ AQUI ESTÁ A VARIÁVEL (Declarada uma vez só) ✨
        LinearLayout.LayoutParams avatarParams = (LinearLayout.LayoutParams) holder.avatarCard.getLayoutParams();

        if (interacao.getId_usuario() == idUsuarioLogado) {
            // --- MINHA MENSAGEM (Direita) ---
            holder.rootLayout.setGravity(Gravity.END);
            holder.rootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            holder.boxMensagem.setBackgroundResource(R.drawable.bg_balao_meu);
            holder.tvMensagem.setTextColor(Color.WHITE);

            // ✨ ESPAÇAMENTO (CORRIGIDO) ✨
            // MarginEnd = Espaço entre Avatar e Balão (8dp para não grudar)
            avatarParams.setMarginEnd(dpToPx(8));
            // MarginStart = Espaço entre Avatar e a Borda da Tela (0dp pois já tem padding no layout pai)
            avatarParams.setMarginStart(0);

        } else {
            // --- MENSAGEM DO OUTRO (Esquerda) ---
            holder.rootLayout.setGravity(Gravity.START);
            holder.rootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

            holder.boxMensagem.setBackgroundResource(R.drawable.bg_balao_outro);
            holder.tvMensagem.setTextColor(Color.BLACK);

            // ✨ ESPAÇAMENTO (CORRIGIDO) ✨
            // MarginEnd = Espaço entre Avatar e Balão (8dp)
            avatarParams.setMarginEnd(dpToPx(8));
            // MarginStart = Espaço entre Avatar e a Borda da Tela (0dp)
            avatarParams.setMarginStart(0);
        }

        // Aplica as margens que configuramos acima
        holder.avatarCard.setLayoutParams(avatarParams);
    }

    @Override
    public int getItemCount() { return listaInteracoes != null ? listaInteracoes.size() : 0; }

    public void atualizarLista(List<InteracaoDTO> novaLista) {
        this.listaInteracoes = novaLista;
        notifyDataSetChanged();
    }

    private String getIniciais(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isEmpty()) return "?";
        String[] partes = nomeCompleto.trim().split("\\s+");
        String iniciais = "" + partes[0].charAt(0);
        if (partes.length > 1) iniciais += partes[partes.length - 1].charAt(0);
        return iniciais.toUpperCase();
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootLayout;
        CardView avatarCard; // Mudou para CardView
        TextView tvAvatarInitials;
        ImageView imgAvatarPhoto; // Novo campo
        LinearLayout boxMensagem;
        TextView tvAutorData;
        TextView tvMensagem;

        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_layout_interacao);
            avatarCard = itemView.findViewById(R.id.avatar_card); // ID novo
            tvAvatarInitials = itemView.findViewById(R.id.tv_avatar_initials);
            imgAvatarPhoto = itemView.findViewById(R.id.img_avatar_photo); // ID novo
            boxMensagem = itemView.findViewById(R.id.box_mensagem);
            tvAutorData = itemView.findViewById(R.id.tv_autor_data);
            tvMensagem = itemView.findViewById(R.id.tv_mensagem);
        }
    }
}