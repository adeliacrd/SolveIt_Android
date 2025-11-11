package com.example.solveit;

import android.content.Intent;
import android.content.SharedPreferences;
// imports desnecessários (Bitmap, Matrix, ExifInterface, InputStream) foram removidos
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

// ✨ IMPORT NECESSÁRIO PARA A NOVA SOLUÇÃO ✨
import com.bumptech.glide.Glide;

import com.google.android.material.textfield.TextInputEditText;

// import java.io.IOException; // Não é mais necessário no bloco try-catch

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    // --- Constantes para clareza ---
    private static final int ROLE_CLIENTE = 1;
    private static final int ROLE_AGENTE = 2;
    private static final int ROLE_ADMIN = 3;

    // --- Componentes da UI (variáveis de instância) ---
    private Toolbar toolbar;
    private TextView profileTextCargo;
    private CircleImageView profileImageAvatar;
    private FrameLayout profileAvatarContainer;

    private TextInputEditText profileEditNome;
    private TextInputEditText profileEditEmail;
    private TextInputEditText profileEditTelefone;
    private TextInputEditText profileEditSenhaAtual;
    private TextInputEditText profileEditNovaSenha;
    private TextInputEditText profileEditArea;

    private LinearLayout profileContainerArea;
    private Button profileButtonAlterar;
    private Button profileButtonSair;

    // Launcher para a galeria de imagens
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Organiza a inicialização em métodos claros
        conectarComponentesDaUI();
        configurarToolbar();
        inicializarImagePicker(); // Este método foi simplificado
        carregarDadosDoUsuario();
        configurarAcoesDosBotoes();
    }

    // O resto da classe permanece igual, apenas o inicializarImagePicker() muda.

    private void conectarComponentesDaUI() {
        toolbar = findViewById(R.id.toolbar_padrao);
        profileTextCargo = findViewById(R.id.profile_text_cargo);
        profileEditNome = findViewById(R.id.profile_edit_nome);
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

    private void configurarToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Seu Perfil");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    // ================================================================================= //
    // ✨ SOLUÇÃO DEFINITIVA COM A BIBLIOTECA GLIDE ✨                                   //
    // ================================================================================= //
    private void inicializarImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        Uri imagemUri = result.getData().getData();

                        // UMA LINHA PARA RESOLVER TUDO:
                        // O Glide carrega a imagem da URI, corrige a rotação automaticamente,
                        // gerencia a memória e a insere no CircleImageView.
                        Glide.with(this)            // Contexto (esta Activity)
                                .load(imagemUri)           // Fonte da imagem (a URI da galeria)
                                .into(profileImageAvatar);   // Onde a imagem será exibida

                        Toast.makeText(this, "Foto de perfil alterada!", Toast.LENGTH_SHORT).show();

                    } else if (result.getResultCode() != RESULT_CANCELED) {
                        // Se não foi cancelado mas deu erro, informa o usuário.
                        Toast.makeText(this, "Falha ao obter a imagem.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void carregarDadosDoUsuario() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        String nomeUsuario = prefs.getString(MainActivity.KEY_USER_NAME, "Usuário");
        String emailUsuario = prefs.getString("user_email_placeholder", "email@exemplo.com");
        String telefoneUsuario = prefs.getString("user_phone_placeholder", "(00) 00000-0000");
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
            default: // ROLE_CLIENTE
                cargo = "Cliente";
                profileContainerArea.setVisibility(View.GONE);
                break;
        }
        profileTextCargo.setText(cargo);
    }

    private void configurarAcoesDosBotoes() {
        profileAvatarContainer.setOnClickListener(v -> abrirGaleria());
        profileButtonAlterar.setOnClickListener(v -> {
            Toast.makeText(this, "Alterações salvas! (Lógica a implementar)", Toast.LENGTH_SHORT).show();
        });
        profileButtonSair.setOnClickListener(v -> fazerLogout());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void fazerLogout() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
