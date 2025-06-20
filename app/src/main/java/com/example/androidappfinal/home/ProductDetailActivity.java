package com.example.androidappfinal.home;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.androidappfinal.R;
import com.example.androidappfinal.models.Product;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            ImageView imgProduct = findViewById(R.id.product_image);
            TextView tvName = findViewById(R.id.product_name);
            TextView tvRating = findViewById(R.id.productRating);
            TextView tvDescription = findViewById(R.id.product_description);
            Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
            ImageButton btnFavorite = findViewById(R.id.btnFavorite);

            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.image_product1)
                    .error(R.drawable.image_product2)
                    .into(imgProduct);

            tvName.setText(product.getName());
            tvRating.setText(String.valueOf(product.getRating()));
            tvDescription.setText(product.getDescription());

            String buttonText = "Add to Cart | " + String.format("%.2f", product.getPrice()) + "$";
            btnAddToCart.setText(buttonText);

            btnFavorite.setImageResource(product.isFavorite()
                    ? R.drawable.ic_favorite
                    : R.drawable.ic_fav_deselect);
        }
    }
}
