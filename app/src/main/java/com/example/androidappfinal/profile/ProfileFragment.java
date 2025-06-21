package com.example.androidappfinal.profile;

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
import com.example.androidappfinal.LoginActivity;
import com.example.androidappfinal.R;
import com.example.androidappfinal.helpers.SessionManager;
import com.example.androidappfinal.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName, profilePhone, profileAddress;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private String userId;
    private DatabaseReference userRef;
    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profilePhone = view.findViewById(R.id.profile_phone);
        profileAddress = view.findViewById(R.id.profile_address);

        profileImage.setOnClickListener(this::changeProfilePicture);
        view.findViewById(R.id.profile_name_edit).setOnClickListener(v -> editField("Name", profileName, "fullName"));
        view.findViewById(R.id.profile_phone_edit).setOnClickListener(v -> editField("Phone", profilePhone, "phone"));
        view.findViewById(R.id.profile_address_edit).setOnClickListener(v -> editField("Address", profileAddress, "address"));
        view.findViewById(R.id.logout_text).setOnClickListener(this::logout);

        setupImagePicker();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
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

    private void loadUserData() {
        SessionManager sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    profileName.setText(user.getFullName());
                    profilePhone.setText(user.getPhone());
                    profileAddress.setText(user.getAddress());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editField(String title, TextView fieldView, String firebaseField) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText input = new EditText(getContext());
        input.setText(fieldView.getText().toString());
        builder.setTitle("Edit " + title)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newValue = input.getText().toString().trim();
                    if (!newValue.isEmpty()) {
                        fieldView.setText(newValue);
                        if (userRef != null) {
                            userRef.child(firebaseField).setValue(newValue);
                        }
                    } else {
                        Toast.makeText(getContext(), title + " cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void changeProfilePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    public void logout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_layout, null);
        builder.setView(dialogView);

        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        AlertDialog dialog = builder.create();

        btnYes.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(requireContext());
            sessionManager.logout();

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