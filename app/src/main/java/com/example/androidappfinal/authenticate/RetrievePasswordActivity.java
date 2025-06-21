package com.example.androidappfinal.authenticate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidappfinal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RetrievePasswordActivity extends AppCompatActivity {

    private EditText emailInput, passwordOutput;
    private TextView passwordLabel, loginLink;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);

        emailInput = findViewById(R.id.emailInput);
        passwordOutput = findViewById(R.id.yourPassword);
        passwordLabel = findViewById(R.id.passwordLabel);
        loginLink = findViewById(R.id.register_loginLink);
        confirmButton = findViewById(R.id.cofirmButton);

        setupPasswordOutput();

        confirmButton.setOnClickListener(v -> checkEmailAndShowPassword());

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void setupPasswordOutput() {
        passwordOutput.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        passwordOutput.setFocusable(false);
        passwordOutput.setCursorVisible(false);
        passwordOutput.setLongClickable(false);
        passwordOutput.setClickable(false);
    }

    private void checkEmailAndShowPassword() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean found = false;
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            String emailFromDB = userSnap.child("email").getValue(String.class);
                            String passwordFromDB = userSnap.child("password").getValue(String.class);

                            if (email.equalsIgnoreCase(emailFromDB)) {
                                showPasswordFields();
                                passwordOutput.setText(passwordFromDB);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            showPasswordFields();
                            passwordOutput.setText("Email not registered");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(RetrievePasswordActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showPasswordFields() {
        passwordLabel.setVisibility(View.VISIBLE);
        passwordOutput.setVisibility(View.VISIBLE);
        loginLink.setVisibility(View.VISIBLE);
    }
}
