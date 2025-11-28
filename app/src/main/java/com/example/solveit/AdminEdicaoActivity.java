package com.example.solveit;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar; // ✅ Importe a classe ActionBar
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminEdicaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edicao);

        // 1. Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        // ✅ CORREÇÃO: Garante que o título padrão da Activity não seja exibido.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 2. Configura a ação de clique para o ícone de navegação (a seta).
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });


        // 3. Encontra os componentes da interface
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        // 4. Cria o adaptador e o conecta ao ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 5. Conecta o TabLayout ao ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
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
