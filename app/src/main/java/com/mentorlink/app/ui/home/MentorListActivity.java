package com.mentorlink.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mentorlink.app.adapter.MentorAdapter;
import com.mentorlink.app.databinding.ActivityMentorListBinding;
import com.mentorlink.app.model.User;
import com.mentorlink.app.ui.auth.LoginActivity;
import com.mentorlink.app.ui.chat.ChatActivity;
import com.mentorlink.app.ui.profile.ProfileActivity;
import com.mentorlink.app.util.Constants;
import com.mentorlink.app.util.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class MentorListActivity extends AppCompatActivity implements MentorAdapter.OnMentorClickListener {

    private ActivityMentorListBinding binding;
    private final List<User> mentors = new ArrayList<>();
    private final List<User> filteredMentors = new ArrayList<>();
    private MentorAdapter adapter;
    private ValueEventListener mentorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMentorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new MentorAdapter(filteredMentors, this);
        binding.recyclerMentors.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerMentors.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadMentors);
        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        binding.btnInbox.setOnClickListener(v -> startActivity(new Intent(this, InboxActivity.class)));
        binding.btnLogout.setOnClickListener(v -> logout());

        setupSearch();
        loadMentors();
    }

    private void setupSearch() {
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String query) {
        filteredMentors.clear();
        if (query.isEmpty()) {
            filteredMentors.addAll(mentors);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (User mentor : mentors) {
                if ((mentor.getName() != null && mentor.getName().toLowerCase().contains(lowerCaseQuery))
                        || (mentor.getExpertise() != null && mentor.getExpertise().toLowerCase().contains(lowerCaseQuery))) {
                    filteredMentors.add(mentor);
                }
            }
        }
        adapter.notifyDataSetChanged();
        binding.emptyView.setVisibility(filteredMentors.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadMentors() {
        if (mentorListener != null) {
            FirebaseUtil.db().child(Constants.DB_USERS).removeEventListener(mentorListener);
        }

        binding.swipeRefresh.setRefreshing(true);
        mentorListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mentors.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User mentor = child.getValue(User.class);
                    if (mentor != null && Constants.ROLE_MENTOR.equals(mentor.getRole())) {
                        mentors.add(mentor);
                    }
                }
                filter(binding.searchView.getQuery().toString());
                binding.swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MentorListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                binding.emptyView.setVisibility(View.VISIBLE);
                binding.swipeRefresh.setRefreshing(false);
            }
        };

        FirebaseUtil.db().child(Constants.DB_USERS)
                .orderByChild("role")
                .equalTo(Constants.ROLE_MENTOR)
                .addValueEventListener(mentorListener);
    }

    private void logout() {
        FirebaseUtil.auth().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mentorListener != null) {
            FirebaseUtil.db().child(Constants.DB_USERS).removeEventListener(mentorListener);
        }
    }

    @Override
    public void onMentorClick(User mentor) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CHAT_USER_ID, mentor.getUid());
        intent.putExtra(Constants.EXTRA_CHAT_USER_NAME, mentor.getName());
        intent.putExtra(Constants.EXTRA_CHAT_USER_ROLE, mentor.getRole());
        intent.putExtra(Constants.EXTRA_CHAT_USER_IMAGE, mentor.getImageUrl());
        startActivity(intent);
    }
}
