package com.example.androidappfinal.favorite;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidappfinal.R;
import com.example.androidappfinal.adapters.FavoriteAdapter;
import com.example.androidappfinal.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private EditText edtSearchFavourite;
    private RecyclerView recyclerViewFavourite;
    private List<Product> fullFavoriteList = new ArrayList<>();
    private List<Product> filteredList = new ArrayList<>();
    private FavoriteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        edtSearchFavourite = view.findViewById(R.id.edtSearchFavourite);
        recyclerViewFavourite = view.findViewById(R.id.recyclerViewFavourite);
        recyclerViewFavourite.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FavoriteAdapter(getContext(), filteredList);
        recyclerViewFavourite.setAdapter(adapter);

        loadFavoriteProducts();
        setupSearchListener();

        return view;
    }

    private void loadFavoriteProducts() {
        FirebaseDatabase.getInstance().getReference("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        fullFavoriteList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Product product = snap.getValue(Product.class);
                            if (product != null && product.isFavorite()) {
                                fullFavoriteList.add(product);
                            }
                        }
                        applyFilter(edtSearchFavourite.getText().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FavoriteFragment", "loadFavoriteProducts: " + error.getMessage());
                    }
                });
    }

    private void setupSearchListener() {
        edtSearchFavourite.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(fullFavoriteList);
        } else {
            for (Product p : fullFavoriteList) {
                if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
