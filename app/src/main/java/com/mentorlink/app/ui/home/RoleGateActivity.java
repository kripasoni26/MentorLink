package com.mentorlink.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mentorlink.app.model.User;
import com.mentorlink.app.ui.auth.LoginActivity;
import com.mentorlink.app.util.Constants;
import com.mentorlink.app.util.FirebaseUtil;

public class RoleGateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseUtil.currentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        FirebaseUtil.db()
                .child(Constants.DB_USERS)
                .child(FirebaseUtil.currentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user == null) {
                            FirebaseUtil.auth().signOut();
                            startActivity(new Intent(RoleGateActivity.this, LoginActivity.class));
                            finish();
                            return;
                        }

                        Intent nextIntent = new Intent(RoleGateActivity.this,
                                Constants.ROLE_STUDENT.equals(user.getRole()) ? MentorListActivity.class : InboxActivity.class);
                        startActivity(nextIntent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RoleGateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}
