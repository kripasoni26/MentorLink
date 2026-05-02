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
import com.mentorlink.app.adapter.InboxAdapter;
import com.mentorlink.app.databinding.ActivityInboxBinding;
import com.mentorlink.app.model.Conversation;
import com.mentorlink.app.model.User;
import com.mentorlink.app.ui.auth.LoginActivity;
import com.mentorlink.app.ui.chat.ChatActivity;
import com.mentorlink.app.ui.profile.ProfileActivity;
import com.mentorlink.app.util.Constants;
import com.mentorlink.app.util.FirebaseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InboxActivity extends AppCompatActivity implements InboxAdapter.OnConversationClickListener {

    private ActivityInboxBinding binding;
    private final List<Conversation> conversations = new ArrayList<>();
    private InboxAdapter adapter;
    private User currentUser;
    private ValueEventListener conversationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerInbox.setLayoutManager(new LinearLayoutManager(this));

        binding.swipeRefresh.setOnRefreshListener(this::loadCurrentUser);
        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        binding.btnMentors.setOnClickListener(v -> startActivity(new Intent(this, MentorListActivity.class)));
        binding.btnLogout.setOnClickListener(v -> logout());

        loadCurrentUser();
    }

    private void loadCurrentUser() {
        if (FirebaseUtil.currentUser() == null) {
            logout();
            return;
        }

        binding.swipeRefresh.setRefreshing(true);
        FirebaseUtil.db().child(Constants.DB_USERS)
                .child(FirebaseUtil.currentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUser = snapshot.getValue(User.class);
                        adapter = new InboxAdapter(
                                conversations,
                                InboxActivity.this,
                                currentUser != null ? currentUser.getUid() : "",
                                currentUser != null ? currentUser.getRole() : ""
                        );
                        binding.recyclerInbox.setAdapter(adapter);
                        binding.btnMentors.setVisibility(
                                currentUser != null && Constants.ROLE_STUDENT.equals(currentUser.getRole())
                                        ? View.VISIBLE
                                        : View.GONE
                        );
                        binding.tvSubtitle.setText(
                                currentUser != null && Constants.ROLE_MENTOR.equals(currentUser.getRole())
                                        ? getString(com.mentorlink.app.R.string.inbox_subtitle_mentor)
                                        : getString(com.mentorlink.app.R.string.inbox_subtitle_student)
                        );
                        loadConversations();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.swipeRefresh.setRefreshing(false);
                    }
                });
    }

    private void loadConversations() {
        if (conversationListener != null) {
            FirebaseUtil.db().child(Constants.DB_CONVERSATIONS).removeEventListener(conversationListener);
        }

        conversationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversations.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Conversation conversation = child.getValue(Conversation.class);
                    if (conversation == null || currentUser == null) {
                        continue;
                    }

                    if (Constants.ROLE_MENTOR.equals(currentUser.getRole())
                            && currentUser.getUid().equals(conversation.getMentorId())) {
                        conversations.add(conversation);
                    } else if (Constants.ROLE_STUDENT.equals(currentUser.getRole())
                            && currentUser.getUid().equals(conversation.getStudentId())) {
                        conversations.add(conversation);
                    }
                }

                Collections.sort(conversations, Comparator.comparingLong(Conversation::getLastUpdated).reversed());
                adapter.notifyDataSetChanged();
                binding.emptyView.setVisibility(conversations.isEmpty() ? View.VISIBLE : View.GONE);
                binding.swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InboxActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                binding.swipeRefresh.setRefreshing(false);
            }
        };

        FirebaseUtil.db().child(Constants.DB_CONVERSATIONS)
                .addValueEventListener(conversationListener);
    }

    private void logout() {
        FirebaseUtil.auth().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conversationListener != null) {
            FirebaseUtil.db().child(Constants.DB_CONVERSATIONS).removeEventListener(conversationListener);
        }
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        if (currentUser == null) {
            return;
        }

        boolean isMentor = Constants.ROLE_MENTOR.equals(currentUser.getRole());

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CHAT_USER_ID, isMentor ? conversation.getStudentId() : conversation.getMentorId());
        intent.putExtra(Constants.EXTRA_CHAT_USER_NAME, isMentor ? conversation.getStudentName() : conversation.getMentorName());
        intent.putExtra(Constants.EXTRA_CHAT_USER_ROLE, isMentor ? Constants.ROLE_STUDENT : Constants.ROLE_MENTOR);
        intent.putExtra(Constants.EXTRA_CHAT_USER_IMAGE, isMentor ? conversation.getStudentImageUrl() : conversation.getMentorImageUrl());
        intent.putExtra(Constants.EXTRA_FROM_INBOX, true);
        startActivity(intent);
    }
}
