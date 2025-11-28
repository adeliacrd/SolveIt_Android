package com.example.solveit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
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
        holder.bind(usuario, this);
    }

    @Override
    public int getItemCount() {
        return listaUsuariosFiltrada != null ? listaUsuariosFiltrada.size() : 0;
    }

    public void atualizarLista(List<Usuario> novaLista) {
        this.listaUsuariosOriginal.clear();
        this.listaUsuariosFiltrada.clear();
        this.listaUsuariosOriginal.addAll(novaLista);
        this.listaUsuariosFiltrada.addAll(novaLista);
        notifyDataSetChanged();
    }


    // =======================================================================
    // MÉTODO DE FILTRO CORRIGIDO
    // =======================================================================
    public void filtrarPorStatus(String statusSelecionado) {
        // Se a opção for "Todos", mostra a lista original completa
        if (statusSelecionado == null || statusSelecionado.equalsIgnoreCase("Todos")) {
            listaUsuariosFiltrada.clear();
            listaUsuariosFiltrada.addAll(listaUsuariosOriginal);
        } else {
            // Se for "Ativos" ou "Inativos", faz a filtragem
            List<Usuario> listaTemporaria = new ArrayList<>();

            // ✅ A CORREÇÃO: Converte a string "Ativos" para o booleano 'true'.
            // Qualquer outra coisa (como "Inativos") resultará em 'false'.
            boolean deveEstarAtivo = statusSelecionado.equalsIgnoreCase("Ativos");

            for (Usuario usuario : listaUsuariosOriginal) {
                // Compara o status do usuário (booleano) com o status desejado (booleano)
                if (usuario.isAtivo() == deveEstarAtivo) {
                    listaTemporaria.add(usuario);
                }
            }
            // Atualiza a lista exibida com o resultado do filtro
            listaUsuariosFiltrada.clear();
            listaUsuariosFiltrada.addAll(listaTemporaria);
        }
        // Notifica o RecyclerView que os dados mudaram para ele se redesenhar
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        // Seu filtro de texto por nome/email/telefone continua o mesmo
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String textoBusca = constraint.toString().toLowerCase().trim();
                List<Usuario> listaTemporaria = new ArrayList<>();
                if (textoBusca.isEmpty()) {
                    listaTemporaria.addAll(listaUsuariosOriginal);
                } else {
                    for (Usuario usuario : listaUsuariosOriginal) {
                        if (usuario.getNome().toLowerCase().contains(textoBusca) ||
                                usuario.getEmail().toLowerCase().contains(textoBusca) ||
                                usuario.getTelefone().toLowerCase().contains(textoBusca)) {
                            listaTemporaria.add(usuario);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listaTemporaria;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listaUsuariosFiltrada.clear();
                listaUsuariosFiltrada.addAll((List<Usuario>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    // =======================================================================
    // VIEWHOLDER (Sem alterações, seu código já está excelente)
    // =======================================================================
    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layoutPrincipalItem;
        private final TextView tvNome, tvEmail, tvTelefone, tvEmpresa;
        private final SwitchMaterial switchStatus;
        private final LinearLayout layoutExpansivel;
        private final AutoCompleteTextView spinnerTipoAcesso;
        private final MaterialButton btnItemConfirmar, btnItemCancelar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutPrincipalItem = itemView.findViewById(R.id.layout_principal_item);
            tvNome = itemView.findViewById(R.id.tv_usuario_nome);
            tvEmail = itemView.findViewById(R.id.tv_usuario_email);
            tvTelefone = itemView.findViewById(R.id.tv_usuario_telefone);
            tvEmpresa = itemView.findViewById(R.id.tv_usuario_empresa);
            switchStatus = itemView.findViewById(R.id.switch_usuario_status);
            layoutExpansivel = itemView.findViewById(R.id.layout_expansivel);
            spinnerTipoAcesso = itemView.findViewById(R.id.spinner_tipo_acesso);
            btnItemConfirmar = itemView.findViewById(R.id.btn_item_confirmar);
            btnItemCancelar = itemView.findViewById(R.id.btn_item_cancelar);
        }

        public void bind(final Usuario usuario, final UsuarioAdapter adapter) {
            tvNome.setText(usuario.getNome());
            tvEmail.setText(usuario.getEmail());
            tvTelefone.setText(usuario.getTelefone());
            tvEmpresa.setText(usuario.getEmpresa());
            switchStatus.setChecked(usuario.isAtivo());
            switchStatus.setText(usuario.isAtivo() ? "Ativo" : "Inativo");

            boolean isExpandido = usuario.isExpandido();
            layoutExpansivel.setVisibility(isExpandido ? View.VISIBLE : View.GONE);

            if (isExpandido) {
                String[] tiposDeAcesso = new String[]{"Usuário", "Agente", "Administrador"};
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                        itemView.getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        tiposDeAcesso
                );
                spinnerTipoAcesso.setAdapter(arrayAdapter);
                spinnerTipoAcesso.setText(usuario.getTipoDeAcesso(), false);
            }

            layoutPrincipalItem.setOnClickListener(v -> {
                usuario.setExpandido(!usuario.isExpandido());
                adapter.notifyItemChanged(getAdapterPosition());
            });

            btnItemConfirmar.setOnClickListener(v -> {
                usuario.setTipoDeAcesso(spinnerTipoAcesso.getText().toString());
                usuario.setExpandido(false);
                adapter.notifyItemChanged(getAdapterPosition());
                Toast.makeText(itemView.getContext(), "Tipo de acesso de " + usuario.getNome() + " atualizado!", Toast.LENGTH_SHORT).show();
            });

            btnItemCancelar.setOnClickListener(v -> {
                usuario.setExpandido(false);
                adapter.notifyItemChanged(getAdapterPosition());
            });

            switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                usuario.setAtivo(isChecked);
                switchStatus.setText(isChecked ? "Ativo" : "Inativo");
                // Opcional: Se quiser que a lista se re-filtre automaticamente ao mudar o switch,
                // precisaria de uma referência ao fragment/activity para chamar o filtro.
                // Por enquanto, esta é a abordagem mais simples e segura.
            });
        }
    }
}
