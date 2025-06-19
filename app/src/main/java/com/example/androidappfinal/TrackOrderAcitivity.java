package com.example.androidappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class TrackOrderAcitivity  extends AppCompatActivity {
    private Button btnTrackOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_check);
        setupUI();
    }

    private void setupUI(){
        btnTrackOrder = findViewById(R.id.button_track_order);
        btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(TrackOrderAcitivity.this, TrackOrderStatusActivity.class);
            startActivity(intent);
        });
    }
}
