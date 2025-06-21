package com.example.androidappfinal.authenticate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidappfinal.base.MainActivity;
import com.example.androidappfinal.R;
import com.example.androidappfinal.helpers.SessionManager;
import com.example.androidappfinal.models.User;
import com.google.firebase.database.FirebaseDatabase;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private TextView loginLink, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.register_emailInput);
        passwordInput = findViewById(R.id.register_passwordInput);
        confirmPasswordInput = findViewById(R.id.register_confirmPasswordInput);
        loginLink = findViewById(R.id.register_loginLink);
        registerButton = findViewById(R.id.register_registerButton);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleRegistration());
        loginLink.setOnClickListener(v -> finish());
    }

    private void handleRegistration() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (isInputInvalid(email, password, confirmPassword)) return;

        String userId = UUID.randomUUID().toString();
        User user = new User(userId, email, email, password, "", "", "customer");

        FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .setValue(user)
                .addOnSuccessListener(aVoid -> {
                    SessionManager sessionManager = new SessionManager(RegisterActivity.this);
                    sessionManager.createLoginSession(userId);

                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private boolean isInputInvalid(String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }
}
