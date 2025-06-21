package com.example.androidappfinal.home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.androidappfinal.R;
import com.example.androidappfinal.helpers.SessionManager;
import com.example.androidappfinal.models.Cart;
import com.example.androidappfinal.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailActivity extends AppCompatActivity {
    private Button btnSmall, btnMedium, btnLarge, btnAddToCart;
    private ImageButton btnFavorite;
    private TextView tvName, tvRating, tvDescription;
    private ImageView imgProduct;
    private double selectedPrice = 0;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            finish();
            return;
        }

        setupViews();
        bindProductData();
        setupFavoriteButton();
        setupSizeButtons();

        selectedPrice = product.getPrice();
        highlightSize(btnSmall);
        updateAddToCartButtonText();

        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void setupViews() {
        imgProduct = findViewById(R.id.product_image);
        tvName = findViewById(R.id.product_name);
        tvRating = findViewById(R.id.productRating);
        tvDescription = findViewById(R.id.product_description);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnSmall = findViewById(R.id.small_size);
        btnMedium = findViewById(R.id.medium_size);
        btnLarge = findViewById(R.id.large_size);
    }

    private void bindProductData() {
        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_product1)
                .error(R.drawable.image_product2)
                .into(imgProduct);

        tvName.setText(product.getName());
        tvRating.setText(String.valueOf(product.getRating()));
        tvDescription.setText(product.getDescription());
        updateFavoriteIcon(product.isFavorite());
    }

    private void setupFavoriteButton() {
        btnFavorite.setOnClickListener(v -> {
            boolean newStatus = !product.isFavorite();
            product.setFavorite(newStatus);
            updateFavoriteIcon(newStatus);

            FirebaseDatabase.getInstance()
                    .getReference("products")
                    .child(product.getId())
                    .child("isFavorite")
                    .setValue(newStatus);
        });
    }

    private void setupSizeButtons() {
        btnSmall.setOnClickListener(v -> {
            highlightSize(btnSmall);
            selectedPrice = product.getPrice();
            updateAddToCartButtonText();
        });

        btnMedium.setOnClickListener(v -> {
            highlightSize(btnMedium);
            selectedPrice = product.getPrice() + 1.0;
            updateAddToCartButtonText();
        });

        btnLarge.setOnClickListener(v -> {
            highlightSize(btnLarge);
            selectedPrice = product.getPrice() + 1.5;
            updateAddToCartButtonText();
        });
    }
    private void highlightSize(Button selectedButton) {
        int gray = Color.parseColor("#A9A9A9");
        btnSmall.setBackgroundTintList(ColorStateList.valueOf(gray));
        btnMedium.setBackgroundTintList(ColorStateList.valueOf(gray));
        btnLarge.setBackgroundTintList(ColorStateList.valueOf(gray));

        int brown = Color.parseColor("#846046");
        selectedButton.setBackgroundTintList(ColorStateList.valueOf(brown));
    }
    private void updateAddToCartButtonText() {
        btnAddToCart.setText("Add to Cart    |    " + String.format("$%.2f", selectedPrice));
    }
    private void updateFavoriteIcon(boolean isFavorite) {
        int iconRes = isFavorite ? R.drawable.ic_favorite : R.drawable.ic_fav_deselect;
        btnFavorite.setImageResource(iconRes);
    }

    private void addToCart() {
        String userId = new SessionManager(this).getUserId();
        if (userId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String size;
        if (selectedPrice == product.getPrice()) {
            size = "S";
        } else if (selectedPrice == product.getPrice() + 1.0) {
            size = "M";
        } else {
            size = "L";
        }

        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(userId)
                .child(product.getId());

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newQuantity = 1;
                if (snapshot.exists()) {
                    Cart existing = snapshot.getValue(Cart.class);
                    if (existing != null) {
                        newQuantity = existing.getQuantity() + 1;
                    }
                }

                Cart cartItem = new Cart(
                        product.getId(),
                        product.getName(),
                        selectedPrice,
                        size,
                        newQuantity,
                        product.getImageUrl(),
                        product.getRating()
                );

                cartRef.setValue(cartItem).addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}