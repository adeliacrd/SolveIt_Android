package com.example.solveit; // !! MUITO IMPORTANTE: Verifique se este é o SEU package name !!

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
// import android.widget.ImageView; // Descomente esta linha se precisar manipular a ImageView via código Java

public class MainActivity extends AppCompatActivity {

    // Declaração dos componentes da UI que usaremos no código Java
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    // private ImageView imageViewLogo; // Descomente se precisar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Esta linha conecta este arquivo Java com o arquivo de layout XML
        setContentView(R.layout.activity_main);

        // Inicialização dos componentes da UI (encontrando-os pelo ID definido no XML)
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        // imageViewLogo = findViewById(R.id.imageViewLogo); // Descomente se precisar

        // Configurar o que acontece quando o botão de login é clicado
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pega os textos dos campos de email e senha
                String email = editTextEmail.getText().toString().trim(); // .trim() remove espaços em branco no início e fim
                String password = editTextPassword.getText().toString().trim();

                // Validação simples dos campos: verifica se não estão vazios
                if (email.isEmpty()) {
                    editTextEmail.setError("Por favor, insira o email."); // Mostra erro direto no campo
                    return; // Para a execução do método onClick aqui se o email estiver vazio
                }

                if (password.isEmpty()) {
                    editTextPassword.setError("Por favor, insira a senha."); // Mostra erro direto no campo
                    return; // Para a execução do método onClick aqui se a senha estiver vazia
                }

                // Lógica de autenticação (exemplo MUITO simples)
                // No mundo real, você verificaria isso contra um banco de dados ou um servidor.
                if (email.equals("teste@solveit.com") && password.equals("123")) {
                    // Se o email E a senha estiverem corretos
                    Toast.makeText(MainActivity.this, "Login bem-sucedido!", Toast.LENGTH_LONG).show();

                    // Aqui você poderia, por exemplo, navegar para uma nova tela (Activity)
                    // Exemplo de como navegar para uma OutraActivity (você precisaria criar essa Activity):
                    // Intent intent = new Intent(MainActivity.this, OutraActivity.class);
                    // startActivity(intent);
                    // finish(); // Opcional: fecha a tela de login para não voltar para ela com o botão "voltar"
                } else {
                    // Se o email ou a senha estiverem incorretos
                    Toast.makeText(MainActivity.this, "Email ou senha inválidos.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
