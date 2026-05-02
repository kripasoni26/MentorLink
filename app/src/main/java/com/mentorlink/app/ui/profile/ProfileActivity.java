package com.mentorlink.app.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mentorlink.app.databinding.ActivityProfileBinding;
import com.mentorlink.app.model.User;
import com.mentorlink.app.util.Constants;
import com.mentorlink.app.util.FirebaseUtil;
import com.mentorlink.app.util.ImageLoader;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveProfile());

        loadProfile();
    }

    private void loadProfile() {
        if (FirebaseUtil.currentUser() == null) {
            finish();
            return;
        }

        setLoading(true);
        FirebaseUtil.db().child(Constants.DB_USERS)
                .child(FirebaseUtil.currentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        setLoading(false);
                        currentUser = snapshot.getValue(User.class);
                        if (currentUser == null) {
                            finish();
                            return;
                        }

                        binding.etName.setText(currentUser.getName());
                        binding.etBio.setText(currentUser.getBio());
                        binding.etExpertise.setText(currentUser.getExpertise());
                        binding.etPhotoUrl.setText(currentUser.getImageUrl());
                        binding.tvRole.setText(currentUser.getRole());
                        binding.tvEmail.setText(currentUser.getEmail());

                        if (!TextUtils.isEmpty(currentUser.getImageUrl())) {
                            ImageLoader.load(binding.ivProfile, currentUser.getImageUrl());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        setLoading(false);
                        toast(error.getMessage());
                    }
                });
    }

    private void saveProfile() {
        if (currentUser == null) {
            return;
        }

        String name = binding.etName.getText().toString().trim();
        String bio = binding.etBio.getText().toString().trim();
        String expertise = binding.etExpertise.getText().toString().trim();
        String imageUrl = binding.etPhotoUrl.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            toast(getString(com.mentorlink.app.R.string.fill_all_fields));
            return;
        }

        setLoading(true);
        currentUser.setName(name);
        currentUser.setBio(bio);
        currentUser.setExpertise(expertise);
        currentUser.setImageUrl(imageUrl);

        FirebaseUtil.db().child(Constants.DB_USERS)
                .child(currentUser.getUid())
                .setValue(currentUser)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    toast(getString(com.mentorlink.app.R.string.profile_updated));
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    toast(e.getMessage());
                });
    }

    private void setLoading(boolean loading) {
        binding.btnSave.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
