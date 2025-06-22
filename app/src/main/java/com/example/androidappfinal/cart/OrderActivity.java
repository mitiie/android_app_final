package com.example.androidappfinal.cart;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidappfinal.R;
import com.example.androidappfinal.adapters.CartAdapter;
import com.example.androidappfinal.helpers.SessionManager;
import com.example.androidappfinal.models.Cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView rvCart;
    private TextView tvTotal;
    private List<Cart> cartItemList;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);

        rvCart = findViewById(R.id.rv_cart_items);
        tvTotal = findViewById(R.id.tv_total);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        cartItemList = new ArrayList<>();

        loadCartItems();

        LinearLayout paymentSelector = findViewById(R.id.payment_selector);
        LinearLayout layoutOther = findViewById(R.id.layout_other_payments);

        paymentSelector.setOnClickListener(v -> {
            if (layoutOther.getVisibility() == View.GONE) {
                layoutOther.setVisibility(View.VISIBLE);
            } else {
                layoutOther.setVisibility(View.GONE);
            }
        });
    }
    private void loadCartItems() {
        String userId = new SessionManager(this).getUserId();

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

                        cartAdapter = new CartAdapter(OrderActivity.this, cartItemList, userId, () -> calculateTotal());
                        rvCart.setAdapter(cartAdapter);
                        calculateTotal();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OrderActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void calculateTotal() {
        double total = 0.0;
        for (Cart item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }

        tvTotal.setText(String.format("Total: %.2f$", total));
    }
}
