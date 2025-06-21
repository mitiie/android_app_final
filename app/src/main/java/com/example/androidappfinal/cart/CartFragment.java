package com.example.androidappfinal.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Button;
import com.example.androidappfinal.R;
import com.example.androidappfinal.adapters.CartAdapter;
import com.example.androidappfinal.authenticate.LoginActivity;
import com.example.androidappfinal.authenticate.RegisterActivity;
import com.example.androidappfinal.models.Cart;
import com.example.androidappfinal.helpers.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView rvCart;
    private TextView tvTotal;
    private CartAdapter cartAdapter;
    private List<Cart> cartItemList;

    private Button btnGoToCart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        rvCart = view.findViewById(R.id.rv_cart_items);
        tvTotal = view.findViewById(R.id.tv_total);
        btnGoToCart = view.findViewById(R.id.btn_go_to_cart);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItemList = new ArrayList<>();

        btnGoToCart.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), OrderActivity.class);
            startActivity(intent);
        });

        loadCartItems();
        return view;
    }

    private void loadCartItems() {
        String userId = new SessionManager(requireContext()).getUserId();
        FirebaseDatabase.getInstance().getReference("carts")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartItemList.clear();
                        for (DataSnapshot itemSnap : snapshot.getChildren()) {
                            Cart item = itemSnap.getValue(Cart.class);
                            if (item != null) {
                                item.setId(itemSnap.getKey());
                                cartItemList.add(item);
                            }
                        }
                        cartAdapter = new CartAdapter(getContext(), cartItemList, userId, () -> calculateTotal());
                        rvCart.setAdapter(cartAdapter);
                        calculateTotal();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private  void calculateTotal() {
        double total = 0.0;

        for (Cart item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }

        tvTotal.setText(String.format("Total: %.2f$", total));
    }
}