package com.example.androidappfinal.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.androidappfinal.R;
import com.example.androidappfinal.helpers.SessionManager;
import com.example.androidappfinal.home.ProductDetailActivity;
import com.example.androidappfinal.models.Cart;
import com.example.androidappfinal.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.productList = products;
    }
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDrink;
        ImageButton btnFavorite, btnAdd;
        TextView tvDrinkName, tvPrice;
        LinearLayout layoutSizes;

        public ProductViewHolder(View itemView) {
            super(itemView);
            imgDrink = itemView.findViewById(R.id.imgDrink);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            tvDrinkName = itemView.findViewById(R.id.tvDrinkName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            layoutSizes = itemView.findViewById(R.id.layoutSizes);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        bindProductData(holder, product);
        handleFavoriteToggle(holder, product);
        setupItemClick(holder, product);
        btnAddTapped(holder, product);
        addToCart(holder, product);
    }

    private void bindProductData(ProductViewHolder holder, Product product) {
        holder.tvDrinkName.setText(product.getName());
        holder.tvPrice.setText(String.format("$ %.2f", product.getPrice()));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_product1)
                .error(R.drawable.image_product2)
                .into(holder.imgDrink);

        updateFavoriteIcon(holder, product.isFavorite());
        displayProductSizes(holder.layoutSizes, product.getSizes());
    }

    private void handleFavoriteToggle(ProductViewHolder holder, Product product) {
        holder.btnFavorite.setOnClickListener(v -> {
            boolean newStatus = !product.isFavorite();
            product.setFavorite(newStatus);
            updateFavoriteIcon(holder, newStatus);

            FirebaseDatabase.getInstance()
                    .getReference("products")
                    .child(product.getId())
                    .child("isFavorite")
                    .setValue(newStatus);
        });
    }

    private void btnAddTapped(ProductViewHolder holder, Product product) {
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    private void setupItemClick(ProductViewHolder holder, Product product) {
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    private void addToCart(ProductViewHolder holder, Product product) {
        holder.btnAdd.setOnClickListener(v -> {
            String userId = new SessionManager(context).getUserId();
            if (userId == null) {
                Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = product.getPrice();

            DatabaseReference cartRef = FirebaseDatabase.getInstance()
                    .getReference("carts")
                    .child(userId)
                    .child(product.getId());

            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int quantity = 1;
                    if (snapshot.exists()) {
                        Cart existing = snapshot.getValue(Cart.class);
                        if (existing != null) {
                            quantity = existing.getQuantity() + 1;
                        }
                    }

                    Cart cartItem = new Cart(
                            product.getId(),
                            product.getName(),
                            price,
                            null,
                            quantity,
                            product.getImageUrl(),
                            product.getRating()
                    );

                    cartRef.setValue(cartItem)
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void updateFavoriteIcon(ProductViewHolder holder, boolean isFavorite) {
        int iconRes = isFavorite ? R.drawable.ic_favorite : R.drawable.ic_fav_deselect;
        holder.btnFavorite.setImageResource(iconRes);
    }

    private void displayProductSizes(LinearLayout layout, List<String> sizes) {
        layout.removeAllViews();

        for (String size : sizes) {
            TextView sizeView = new TextView(context);
            sizeView.setText(size);
            sizeView.setTextSize(13f);
            sizeView.setTextColor(Color.BLACK);
            sizeView.setPadding(12, 4, 12, 4);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd(8);
            sizeView.setLayoutParams(params);

            layout.addView(sizeView);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}