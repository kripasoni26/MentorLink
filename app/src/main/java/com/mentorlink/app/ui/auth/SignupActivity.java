package com.mentorlink.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.mentorlink.app.databinding.ActivitySignupBinding;
import com.mentorlink.app.model.User;
import com.mentorlink.app.ui.home.RoleGateActivity;
import com.mentorlink.app.util.Constants;
import com.mentorlink.app.util.FirebaseUtil;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Chip mentorChip = binding.chipMentor;
        Chip studentChip = binding.chipStudent;
        studentChip.setChecked(true);

        mentorChip.setOnClickListener(v -> {
            mentorChip.setChecked(true);
            studentChip.setChecked(false);
        });

        studentChip.setOnClickListener(v -> {
            studentChip.setChecked(true);
            mentorChip.setChecked(false);
        });

        binding.btnSignup.setOnClickListener(v -> signup());
        binding.tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void signup() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String role = binding.chipMentor.isChecked() ? Constants.ROLE_MENTOR : Constants.ROLE_STUDENT;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            toast(getString(com.mentorlink.app.R.string.fill_all_fields));
            return;
        }

        if (password.length() < 6) {
            toast(getString(com.mentorlink.app.R.string.password_short));
            return;
        }

        setLoading(true);

        FirebaseUtil.auth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser() != null ? authResult.getUser().getUid() : "";
                    User user = new User(uid, name, email, role);
                    user.setBio(role.equals(Constants.ROLE_MENTOR)
                            ? getString(com.mentorlink.app.R.string.default_mentor_bio)
                            : getString(com.mentorlink.app.R.string.default_student_bio));

                    saveUser(user);
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    toast(e.getMessage());
                });
    }

    private void saveUser(User user) {
        FirebaseUtil.db()
                .child(Constants.DB_USERS)
                .child(user.getUid())
                .setValue(user)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    startActivity(new Intent(this, RoleGateActivity.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    toast(e.getMessage());
                });
    }

    private void setLoading(boolean loading) {
        binding.btnSignup.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
