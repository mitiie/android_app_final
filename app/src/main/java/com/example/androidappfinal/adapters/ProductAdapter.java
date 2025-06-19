package com.example.androidappfinal.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.androidappfinal.R;
import com.example.androidappfinal.models.Product;
import com.google.firebase.database.FirebaseDatabase;
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

        holder.tvDrinkName.setText(product.getName());
        holder.tvPrice.setText(String.format("$ %.2f", product.getPrice()));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_product1)
                .error(R.drawable.image_product2)
                .into(holder.imgDrink);

        updateFavoriteIcon(holder, product.isFavorite());

        holder.btnFavorite.setOnClickListener(v -> {
            boolean newFavoriteStatus = !product.isFavorite();
            product.setFavorite(newFavoriteStatus);
            updateFavoriteIcon(holder, newFavoriteStatus);

            FirebaseDatabase.getInstance()
                    .getReference("products")
                    .child(product.getId())
                    .child("isFavorite")
                    .setValue(newFavoriteStatus);
        });

        displayProductSizes(holder.layoutSizes, product.getSizes());
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