package com.example.androidappfinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView profileName;
    private TextView profilePhone;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupViews();
        setupImagePicker();
    }

    private void setupViews() {
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profilePhone = findViewById(R.id.profile_phone);
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        profileImage.setImageURI(imageUri);
                    }
                }
        );
    }


    public void changeProfilePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    public void editName(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setText(profileName.getText().toString());
        builder.setView(input)
                .setTitle("Edit Name")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString();
                    if (!newName.isEmpty()) {
                        profileName.setText(newName);
                    } else {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void editPhone(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setText(profilePhone.getText().toString());
        builder.setView(input)
                .setTitle("Edit Phone")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newPhone = input.getText().toString();
                    if (!newPhone.isEmpty()) {
                        profilePhone.setText(newPhone);
                    } else {
                        Toast.makeText(this, "Phone cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void logout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
        builder.setView(dialogView);

        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        AlertDialog dialog = builder.create();

        btnYes.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}