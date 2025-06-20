package com.example.androidappfinal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.androidappfinal.R;
import com.example.androidappfinal.models.Product;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavViewHolder> {
    private Context context;
    private List<Product> favoriteList;

    public FavoriteAdapter(Context context, List<Product> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    public static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productRating, tvQuantity;
        ShapeableImageView favouriteIcon, btnLess, btnAdd;
        LinearLayout layoutFavouriteOrQuantity;

        public FavViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productRating = itemView.findViewById(R.id.productRating);
            favouriteIcon = itemView.findViewById(R.id.favouriteIcon);
            btnLess = itemView.findViewById(R.id.btn_less);
            btnAdd = itemView.findViewById(R.id.btn_add);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            layoutFavouriteOrQuantity = itemView.findViewById(R.id.layout_favourite_or_quantity);
        }
    }

    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavViewHolder holder, int position) {
        Product product = favoriteList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("$ %.2f", product.getPrice()));
        holder.productRating.setText(String.format("%.1f", product.getRating()));
        holder.btnLess.setVisibility(View.GONE);
        holder.tvQuantity.setVisibility(View.GONE);

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.image_product1)
                .error(R.drawable.image_product2)
                .into(holder.productImage);

        holder.favouriteIcon.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                FirebaseDatabase.getInstance()
                        .getReference("products")
                        .child(product.getId())
                        .child("isFavorite")
                        .setValue(false);

                favoriteList.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }
}

