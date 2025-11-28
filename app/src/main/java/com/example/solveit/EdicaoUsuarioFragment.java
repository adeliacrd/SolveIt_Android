package com.example.solveit;

import android.os.Bundle;
import android.view.LayoutInflater;import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EdicaoUsuarioFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Apenas infla o layout de placeholder que criamos
        return inflater.inflate(R.layout.fragment_edicao_usuario, container, false);
    }
}
