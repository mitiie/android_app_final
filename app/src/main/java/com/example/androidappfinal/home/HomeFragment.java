package com.example.androidappfinal.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidappfinal.R;
import com.example.androidappfinal.adapters.ProductAdapter;
import com.example.androidappfinal.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.LinearLayout;

public class HomeFragment extends Fragment {
    private EditText edtSearch;
    private RecyclerView rvItems;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> productListFilter = new ArrayList<>();
    private DatabaseReference databaseRef;
    private LinearLayout btnCoffee, btnDesserts, btnAlcohol, btnTea, btnBreakfast;
    private List<LinearLayout> categoryButtons = new ArrayList<>();
    private LinearLayout currentSelectedBtn = null;
    private String currentCategory = "ALL";
    private String currentSearchQuery = "";

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupViews(view);
        setupRecyclerView();
        setupSearch();
        loadProducts();

        return view;
    }

    private void setupViews(View view) {
        rvItems = view.findViewById(R.id.rvItems);
        edtSearch = view.findViewById(R.id.edtSearch);
        databaseRef = FirebaseDatabase.getInstance().getReference("products");

        btnCoffee = view.findViewById(R.id.btnCoffee);
        btnDesserts = view.findViewById(R.id.btnDesserts);
        btnAlcohol = view.findViewById(R.id.btnAlcohol);
        btnTea = view.findViewById(R.id.btnTea);
        btnBreakfast = view.findViewById(R.id.btnBreakfast);

        categoryButtons.add(btnCoffee);
        categoryButtons.add(btnDesserts);
        categoryButtons.add(btnAlcohol);
        categoryButtons.add(btnTea);
        categoryButtons.add(btnBreakfast);

        btnCoffee.setOnClickListener(v -> onCategorySelected("COFFEE", btnCoffee));
        btnDesserts.setOnClickListener(v -> onCategorySelected("DESSERTS", btnDesserts));
        btnAlcohol.setOnClickListener(v -> onCategorySelected("ALCOHOL", btnAlcohol));
        btnTea.setOnClickListener(v -> onCategorySelected("TEA", btnTea));
        btnBreakfast.setOnClickListener(v -> onCategorySelected("BREAKFAST", btnBreakfast));
    }

    private void setupRecyclerView() {
        rvItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductAdapter(getContext(), productListFilter);
        rvItems.setAdapter(adapter);
    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                applyFilters();
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProducts() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProducts.clear();
                for (DataSnapshot productSnap : snapshot.getChildren()) {
                    Product product = productSnap.getValue(Product.class);
                    if (product != null) {
                        allProducts.add(product);
                    }
                }
                applyFilters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load products: " + error.getMessage());
            }
        });
    }

    private void onCategorySelected(String category, LinearLayout selectedBtn) {
        if (currentSelectedBtn == selectedBtn) {
            currentCategory = "ALL";
            currentSelectedBtn = null;

            for (LinearLayout btn : categoryButtons) {
                btn.setBackgroundResource(R.drawable.bg_category_btn);
            }

            applyFilters();
            return;
        }

        currentCategory = category;
        currentSelectedBtn = selectedBtn;

        for (LinearLayout btn : categoryButtons) {
            btn.setBackgroundResource(R.drawable.bg_category_btn);
        }

        selectedBtn.setBackgroundResource(R.drawable.bg_category_btn_selected);
        applyFilters();
    }

    private void applyFilters() {
        productListFilter.clear();

        for (Product p : allProducts) {
            boolean matchCategory = currentCategory.equalsIgnoreCase("ALL") ||
                    p.getCategory().equalsIgnoreCase(currentCategory);
            boolean matchSearch = p.getName().toLowerCase().contains(currentSearchQuery.toLowerCase());

            if (matchCategory && matchSearch) {
                productListFilter.add(p);
            }
        }

        adapter.notifyDataSetChanged();
    }
}