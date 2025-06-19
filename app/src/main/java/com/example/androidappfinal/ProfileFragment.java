package com.example.androidappfinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName;
    private TextView profilePhone;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profilePhone = view.findViewById(R.id.profile_phone);

        profileImage.setOnClickListener(this::changeProfilePicture);
        view.findViewById(R.id.profile_name_edit).setOnClickListener(this::editName);
        view.findViewById(R.id.profile_phone_edit).setOnClickListener(this::editPhone);
        view.findViewById(R.id.logout_text).setOnClickListener(this::logout);

        setupImagePicker();

        return view;
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText input = new EditText(getContext());
        input.setText(profileName.getText().toString());
        builder.setView(input)
                .setTitle("Edit Name")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString();
                    if (!newName.isEmpty()) {
                        profileName.setText(newName);
                    } else {
                        Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void editPhone(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText input = new EditText(getContext());
        input.setText(profilePhone.getText().toString());
        builder.setView(input)
                .setTitle("Edit Phone")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newPhone = input.getText().toString();
                    if (!newPhone.isEmpty()) {
                        profilePhone.setText(newPhone);
                    } else {
                        Toast.makeText(getContext(), "Phone cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void logout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_layout, null);
        builder.setView(dialogView);

        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        AlertDialog dialog = builder.create();

        btnYes.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}