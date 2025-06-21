package com.example.androidappfinal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.androidappfinal.R;
import com.example.androidappfinal.models.Cart;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<Cart> cartItemList;
    private String userId;
    private Runnable onCartChanged;

    public CartAdapter(Context context, List<Cart> cartItemList, String userId, Runnable onCartChanged) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.userId = userId;
        this.onCartChanged = onCartChanged;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart item = cartItemList.get(position);

        holder.productName.setText(item.getName());
        holder.productPrice.setText(item.getPrice() + "$");
        holder.productRating.setText(String.valueOf(item.getRating()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        Glide.with(context).load(item.getImageUrl()).into(holder.productImage);

        holder.btnAdd.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            holder.tvQuantity.setText(String.valueOf(newQuantity));
            updateQuantity(item);
        });

        holder.btnLess.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                updateQuantity(item);
            } else {
                FirebaseDatabase.getInstance()
                        .getReference("carts")
                        .child(userId)
                        .child(item.getId())
                        .removeValue()
                        .addOnSuccessListener(aVoid -> {
                            cartItemList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            if (onCartChanged != null) onCartChanged.run();
                            Toast.makeText(context, "Product removed from cart", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void updateQuantity(Cart item) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(userId)
                .child(item.getId())
                .child("quantity");

        cartRef.setValue(item.getQuantity()).addOnSuccessListener(aVoid -> {
            if (onCartChanged != null) onCartChanged.run();
        }).addOnFailureListener(e ->
                Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productRating, tvQuantity;
        ImageView btnAdd, btnLess;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productRating = itemView.findViewById(R.id.productRating);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnAdd = itemView.findViewById(R.id.btn_add);
            btnLess = itemView.findViewById(R.id.btn_less);
        }
    }
}