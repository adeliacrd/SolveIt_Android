package com.example.solveit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // <-- IMPORTAÇÃO NECESSÁRIA

import com.google.android.material.textfield.TextInputEditText;

// ATENÇÃO: a sua classe usa AutoCompleteTextView para o e-mail, então vamos manter.
import android.widget.AutoCompleteTextView;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    // --- IDs dos Tipos de Acesso ---
    private static final int ROLE_CLIENTE = 1;
    private static final int ROLE_AGENTE = 2;
    private static final int ROLE_ADMIN = 3;

    // --- Chaves para SharedPreferences ---
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_PHONE = "user_phone";

    // --- Componentes da UI ---
    private TextView profileTextCargo;
    private TextInputEditText profileEditNome, profileEditTelefone,
            profileEditSenhaAtual, profileEditNovaSenha, profileEditArea;
    // O seu XML original usa TextInputEditText, mas o código antigo usava AutoComplete.
    // Vamos usar o que está no seu código anterior para manter a funcionalidade de sugestão.
    // Se o ID no XML for diferente, ajuste-o.
    private AutoCompleteTextView profileEditEmail;
    private LinearLayout profileContainerArea;
    private Button profileButtonAlterar, profileButtonSair;

    private FrameLayout profileAvatarContainer;
    private CircleImageView profileImageAvatar;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =======================================================
        // ✨ PASSO 1: CARREGAR O LAYOUT VISUAL CORRETO ✨
        // =======================================================
        setContentView(R.layout.activity_perfil);

        // =======================================================
        // ✨ PASSO 2: CONFIGURAR A TOOLBAR ✨
        // =======================================================
        configurarToolbar();

        // =======================================================
        // ✨ PASSO 3: CONECTAR E CONFIGURAR O RESTO DA TELA ✨
        // =======================================================
        conectarComponentes();
        inicializarImagePicker();
        carregarDadosEConfigurarTela();
        configurarAcoesDosBotoes();
    }

    private void configurarToolbar() {
        // Encontra a toolbar pelo ID que está no layout incluído (toolbar_perfil.xml)
        Toolbar toolbar = findViewById(R.id.toolbar_padrao);
        if (toolbar != null) {
            toolbar.setTitle("Seu Perfil");
            toolbar.setNavigationOnClickListener(v -> finish()); // Ação de voltar
        }
    }

    private void conectarComponentes() {
        profileTextCargo = findViewById(R.id.profile_text_cargo);
        profileEditNome = findViewById(R.id.profile_edit_nome);

        // O seu XML usa TextInputEditText mas o código anterior AutoComplete.
        // Vamos assumir que o ID é o mesmo.
        // Se `profile_edit_email` for um TextInputEditText no XML, o app pode quebrar aqui.
        // Se quebrar, troque para TextInputEditText aqui também.
        profileEditEmail = findViewById(R.id.profile_edit_email);

        profileEditTelefone = findViewById(R.id.profile_edit_telefone);
        profileEditSenhaAtual = findViewById(R.id.profile_edit_senha_atual);
        profileEditNovaSenha = findViewById(R.id.profile_edit_nova_senha);
        profileContainerArea = findViewById(R.id.profile_container_area);
        profileEditArea = findViewById(R.id.profile_edit_area);
        profileButtonAlterar = findViewById(R.id.profile_button_alterar);
        profileButtonSair = findViewById(R.id.profile_button_sair);

        profileAvatarContainer = findViewById(R.id.profile_avatar_container);
        profileImageAvatar = findViewById(R.id.profile_image_avatar);
    }

    // O resto do seu código permanece igual, pois ele já estava correto.
    // ... (cole aqui o resto dos seus métodos sem alteração) ...
    private void inicializarImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        Uri imagemUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemUri);
                            profileImageAvatar.setImageBitmap(bitmap);
                            Toast.makeText(this, "Foto de perfil alterada!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Falha ao carregar a imagem.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void carregarDadosEConfigurarTela() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String nomeUsuario = prefs.getString(MainActivity.KEY_USER_NAME, "");
        String emailUsuario = prefs.getString(KEY_USER_EMAIL, "");
        String telefoneUsuario = prefs.getString(KEY_USER_PHONE, "");
        int idTipoAcesso = prefs.getInt(MainActivity.KEY_USER_ROLE_ID, ROLE_CLIENTE);

        profileEditNome.setText(nomeUsuario);
        profileEditEmail.setText(emailUsuario);
        profileEditTelefone.setText(telefoneUsuario);

        String cargo;
        switch (idTipoAcesso) {
            case ROLE_ADMIN:
                cargo = "Administrador";
                profileContainerArea.setVisibility(View.VISIBLE);
                break;
            case ROLE_AGENTE:
                cargo = "Agente de Suporte";
                profileContainerArea.setVisibility(View.GONE);
                break;
            default:
                cargo = "Cliente";
                profileContainerArea.setVisibility(View.GONE);
                break;
        }
        profileTextCargo.setText(cargo);
        configurarAutoCompleteEmail(nomeUsuario);
    }

    private void configurarAutoCompleteEmail(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) return;
        String nomeNormalizado = Normalizer.normalize(nomeCompleto, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "").toLowerCase();
        String[] partesNome = nomeNormalizado.split("\\s+");
        if (partesNome.length < 1) return;
        String primeiroNome = partesNome[0];
        String ultimoNome = partesNome.length > 1 ? partesNome[partesNome.length - 1] : "";
        String dominio = "@gmail.com";
        Set<String> sugestoes = new HashSet<>();
        if (!ultimoNome.isEmpty()) {
            sugestoes.add(primeiroNome + "." + ultimoNome + dominio);
            sugestoes.add(primeiroNome + ultimoNome + dominio);
        }
        sugestoes.add(primeiroNome + "_dev" + dominio);
        sugestoes.add(primeiroNome + (int) (Math.random() * 100) + dominio);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(sugestoes));
        profileEditEmail.setAdapter(adapter);
    }

    private void configurarAcoesDosBotoes() {
        profileAvatarContainer.setOnClickListener(v -> abrirGaleria());
        profileButtonAlterar.setOnClickListener(v -> Toast.makeText(this, "Botão 'Alterar Informações' clicado.", Toast.LENGTH_SHORT).show());
        profileButtonSair.setOnClickListener(v -> fazerLogout());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void fazerLogout() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
