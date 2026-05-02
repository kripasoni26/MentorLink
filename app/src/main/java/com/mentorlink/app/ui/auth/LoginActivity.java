package com.mentorlink.app.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mentorlink.app.databinding.ActivityLoginBinding;
import com.mentorlink.app.ui.home.RoleGateActivity;
import com.mentorlink.app.util.Constants;
import com.mentorlink.app.util.FirebaseUtil;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseUtil.currentUser() != null) {
            openRoleGate();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> login());
        binding.tvGoToSignup.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }

    private void login() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            toast(getString(com.mentorlink.app.R.string.fill_all_fields));
            return;
        }

        setLoading(true);

        FirebaseUtil.auth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        openRoleGate(); // Or openRoleGate() if you removed FCM
                    } else {
                        setLoading(false);
                        String error = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        toast(error);
                    }
                });
    }

    private void setLoading(boolean loading) {
        binding.btnLogin.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void openRoleGate() {
        if (FirebaseUtil.currentUser() == null) {
            setLoading(false);
            return;
        }

        FirebaseUtil.db().child(Constants.DB_USERS)
                .child(FirebaseUtil.currentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        setLoading(false);
                        if (!snapshot.exists()) {
                            FirebaseUtil.auth().signOut();
                            toast(getString(com.mentorlink.app.R.string.profile_missing));
                            return;
                        }

                        startActivity(new Intent(LoginActivity.this, RoleGateActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        setLoading(false);
                        toast(error.getMessage());
                    }
                });
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
