package com.example.solveit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    // O construtor recebe a Activity que vai hospedar o ViewPager
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Este é o método principal. Ele decide qual fragmento criar baseado na posição da aba.
        // Posição 0 é a primeira aba, 1 é a segunda, e assim por diante.
        switch (position) {
            case 1:
                return new EdicaoUsuarioFragment(); // Segunda aba
            case 0:
            default:
                return new EdicaoChamadoFragment(); // Primeira aba (e padrão)
        }
    }

    @Override
    public int getItemCount() {
        // Retorna o número total de abas que você terá.
        return 2;
    }
}
