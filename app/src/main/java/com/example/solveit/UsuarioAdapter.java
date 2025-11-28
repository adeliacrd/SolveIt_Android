package com.example.solveit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> implements Filterable {

    private final List<Usuario> listaUsuariosOriginal;
    private List<Usuario> listaUsuariosFiltrada;

    public UsuarioAdapter(List<Usuario> listaUsuarios) {
        this.listaUsuariosOriginal = new ArrayList<>(listaUsuarios);
        this.listaUsuariosFiltrada = new ArrayList<>(listaUsuarios);
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuariosFiltrada.get(position);
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {
        return listaUsuariosFiltrada != null ? listaUsuariosFiltrada.size() : 0;
    }

    // =======================================================================
    // FILTRO PARA O DROPDOWN DE STATUS (Ativo/Inativo)
    // =======================================================================
    public void filtrarPorStatus(String status) {
        List<Usuario> listaTemporaria = new ArrayList<>();

        if (status == null || status.equalsIgnoreCase("Todos")) {
            // Se o status for nulo ou "Todos", mostra a lista original completa
            listaTemporaria.addAll(listaUsuariosOriginal);
        } else {
            boolean isAtivo = status.equalsIgnoreCase("Ativo");
            for (Usuario usuario : listaUsuariosOriginal) {
                // Compara o status do usuário com o status desejado
                if (usuario.isAtivo() == isAtivo) {
                    listaTemporaria.add(usuario);
                }
            }
        }

        // Atualiza a lista filtrada e notifica o RecyclerView
        listaUsuariosFiltrada.clear();
        listaUsuariosFiltrada.addAll(listaTemporaria);
        notifyDataSetChanged();
    }


    // =======================================================================
    // FILTRO DE BUSCA POR TEXTO (CÓDIGO COMPLETO E CORRIGIDO)
    // =======================================================================
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String textoBusca = constraint.toString().toLowerCase().trim();
                List<Usuario> listaTemporaria = new ArrayList<>();

                if (textoBusca.isEmpty()) {
                    // Se a busca estiver vazia, usa a lista original completa
                    listaTemporaria.addAll(listaUsuariosOriginal);
                } else {
                    // Percorre a lista original procurando por correspondências
                    for (Usuario usuario : listaUsuariosOriginal) {
                        if (usuario.getNome().toLowerCase().contains(textoBusca) ||
                                usuario.getEmail().toLowerCase().contains(textoBusca) ||
                                usuario.getTelefone().toLowerCase().contains(textoBusca)) {
                            listaTemporaria.add(usuario);
                        }
                    }
                }

                // Cria o objeto de resultados e o retorna
                FilterResults filterResults = new FilterResults();
                filterResults.values = listaTemporaria;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // Atualiza a lista filtrada com os resultados
                listaUsuariosFiltrada.clear();
                listaUsuariosFiltrada.addAll((List<Usuario>) results.values);
                // Notifica o RecyclerView para se redesenhar
                notifyDataSetChanged();
            }
        };
    }

    // =======================================================================
    // VIEWHOLDER (A estrutura que segura os componentes de cada item)
    // =======================================================================
    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNome, tvEmail, tvTelefone, tvEmpresa;
        private final SwitchMaterial switchStatus;
        private final ImageView ivEdit;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tv_usuario_nome);
            tvEmail = itemView.findViewById(R.id.tv_usuario_email);
            tvTelefone = itemView.findViewById(R.id.tv_usuario_telefone);
            tvEmpresa = itemView.findViewById(R.id.tv_usuario_empresa);
            switchStatus = itemView.findViewById(R.id.switch_usuario_status);
            ivEdit = itemView.findViewById(R.id.iv_usuario_edit);
        }

        public void bind(final Usuario usuario) {
            tvNome.setText(usuario.getNome());
            tvEmail.setText(usuario.getEmail());
            tvTelefone.setText(usuario.getTelefone());
            tvEmpresa.setText(usuario.getEmpresa());
            switchStatus.setChecked(usuario.isAtivo());
            switchStatus.setText(usuario.isAtivo() ? "Ativo" : "Inativo");

            // Permite que o usuário mude o status diretamente no switch
            switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                usuario.setAtivo(isChecked);
                switchStatus.setText(isChecked ? "Ativo" : "Inativo");
            });

            // Ação do clique no ícone de editar (lápis)
            ivEdit.setOnClickListener(v -> {
                // Futuramente, aqui você pode abrir uma nova tela para editar
                // os detalhes completos do usuário.
            });
        }
    }
}
