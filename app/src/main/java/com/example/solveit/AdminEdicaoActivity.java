package com.example.solveit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminEdicaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Infla o layout principal que contém a Toolbar, o TabLayout e o ViewPager2
        setContentView(R.layout.activity_admin_edicao);

        // 2. Configura a Toolbar (se houver alguma lógica específica, como botões de menu)
        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        // 3. Encontra os componentes da interface
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        // 4. Cria uma instância do nosso adaptador
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        // E conecta o adaptador ao ViewPager2
        viewPager.setAdapter(adapter);

        // 5. A MÁGICA FINAL: Conecta o TabLayout ao ViewPager2
        // Isso faz com que as abas sejam criadas, seus títulos sejam definidos
        // e o clique em uma aba atualize o ViewPager (e vice-versa).
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Define o texto de cada aba baseado na sua posição
            switch (position) {
                case 0:
                    tab.setText("Editar Chamado");
                    break;
                case 1:
                    tab.setText("Editar Usuários");
                    break;
            }
        }).attach();
    }
}
