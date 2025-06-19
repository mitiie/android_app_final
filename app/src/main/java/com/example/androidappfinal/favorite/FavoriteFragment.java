package com.example.androidappfinal.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidappfinal.R;

public class FavoriteFragment extends Fragment {
    private EditText edtSearchFavourite;
    private RecyclerView recyclerViewFavourite;

    public FavoriteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        edtSearchFavourite = view.findViewById(R.id.edtSearchFavourite);
        recyclerViewFavourite = view.findViewById(R.id.recyclerViewFavourite);

        return view;
    }
}