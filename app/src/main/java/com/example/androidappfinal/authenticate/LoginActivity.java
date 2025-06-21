package com.example.androidappfinal.authenticate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidappfinal.base.MainActivity;
import com.example.androidappfinal.R;
import com.example.androidappfinal.helpers.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private Button registerButton;
    private Button loginButton;
    private EditText edtPassword;
    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setupUI();
    }
    private void setupUI() {
        edtEmail = findViewById(R.id.emailInput);
        edtPassword = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> loginUser());

        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RetrievePasswordActivity.class);
            startActivity(intent);
        });
    }
    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean found = false;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String dbEmail = userSnapshot.child("email").getValue(String.class);
                            String dbPassword = userSnapshot.child("password").getValue(String.class);

                            if (email.equals(dbEmail) && password.equals(dbPassword)) {
                                String role = userSnapshot.child("role").getValue(String.class);
                                found = true;

                                if ("customer".equals(role)) {
                                    String userId = userSnapshot.getKey();
                                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                    sessionManager.createLoginSession(userId);

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Unknown user role.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }

                        if (!found) {
                            Toast.makeText(LoginActivity.this, "Incorrect email or password.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("LOGIN_ERROR", "Login failed: " + error.getMessage());
                        Toast.makeText(LoginActivity.this, "System error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}