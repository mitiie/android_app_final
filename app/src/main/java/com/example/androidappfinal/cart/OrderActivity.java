package com.example.androidappfinal.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.androidappfinal.R;
import com.example.androidappfinal.adapters.CartAdapter;
import com.example.androidappfinal.helpers.SessionManager;
import com.example.androidappfinal.models.Cart;
import com.example.androidappfinal.models.User;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView rvCart;
    private TextView tvTotal, addressText;
    private List<Cart> cartItemList;
    private CartAdapter cartAdapter;
    private LinearLayout paymentSelector, layoutBankTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);

        initViews();
        setupRecyclerView();
        setupPaymentMethods();
        loadCartItems();
        loadUserAddress();
    }

    private void initViews() {
        rvCart = findViewById(R.id.rv_cart_items);
        tvTotal = findViewById(R.id.tv_total);
        addressText = findViewById(R.id.profile_address);
        paymentSelector = findViewById(R.id.payment_selector);
        layoutBankTransfer = findViewById(R.id.layout_bank_transfer);
    }

    private void setupRecyclerView() {
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        cartItemList = new ArrayList<>();
    }

    private void setupPaymentMethods() {
        paymentSelector.setOnClickListener(v -> {
            int visibility = layoutBankTransfer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
            layoutBankTransfer.setVisibility(visibility);
        });

        layoutBankTransfer.setOnClickListener(v -> showQrDialog());
    }

    private void loadUserAddress() {
        String userId = new SessionManager(this).getUserId();

        findViewById(R.id.btn_creat_order).setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, TrackOrderActivity.class);
            startActivity(intent);
        });

        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            addressText.setText(user.getAddress());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OrderActivity.this, "Failed to load address", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCartItems() {
        String userId = new SessionManager(this).getUserId();

        FirebaseDatabase.getInstance().getReference("carts").child(userId)
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

    private void showQrDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_qr_code, null);
        ImageView qrImage = dialogView.findViewById(R.id.qr_image);
        ImageView btnClose = dialogView.findViewById(R.id.btn_close_qr);

        String qrUrl = "https://qr.sepay.vn/img?bank=MBBank&acc=15615062004&template=compact&amount=&des=";
        Glide.with(this).load(qrUrl).into(qrImage);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
