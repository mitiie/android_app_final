package com.example.androidappfinal.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidappfinal.MainActivity;
import com.example.androidappfinal.R;
import com.example.androidappfinal.helpers.SessionManager;

public class OrderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
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
}
