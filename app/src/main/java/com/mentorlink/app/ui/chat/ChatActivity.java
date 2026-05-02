package com.mentorlink.app.ui.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mentorlink.app.adapter.MessageAdapter;
import com.mentorlink.app.databinding.ActivityChatBinding;
import com.mentorlink.app.model.Conversation;
import com.mentorlink.app.model.Message;
import com.mentorlink.app.model.User;
import com.mentorlink.app.util.Constants;
import com.mentorlink.app.util.FirebaseUtil;
import com.mentorlink.app.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private final List<Message> messages = new ArrayList<>();
    private final Set<String> messageIds = new HashSet<>();
    private MessageAdapter adapter;
    private String peerId;
    private String peerName;
    private String peerImage;
    private String conversationId;
    private User currentUser;
    private User peerUser;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        peerId = getIntent().getStringExtra(Constants.EXTRA_CHAT_USER_ID);
        peerName = getIntent().getStringExtra(Constants.EXTRA_CHAT_USER_NAME);
        peerImage = getIntent().getStringExtra(Constants.EXTRA_CHAT_USER_IMAGE);

        if (FirebaseUtil.currentUser() == null || TextUtils.isEmpty(peerId)) {
            finish();
            return;
        }

        conversationId = FirebaseUtil.conversationId(FirebaseUtil.currentUser().getUid(), peerId);

        adapter = new MessageAdapter(messages, FirebaseUtil.currentUser().getUid());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerMessages.setLayoutManager(layoutManager);
        binding.recyclerMessages.setAdapter(adapter);

        binding.toolbar.setOnClickListener(v -> finish());
        binding.tvName.setText(peerName);
        if (!TextUtils.isEmpty(peerImage)) {
            ImageLoader.load(binding.ivAvatar, peerImage);
        }

        binding.btnSend.setOnClickListener(v -> sendMessage());

        loadUsersAndMessages();
    }

    private void loadUsersAndMessages() {
        FirebaseUtil.db().child(Constants.DB_USERS)
                .child(FirebaseUtil.currentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUser = snapshot.getValue(User.class);
                        loadPeer();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        toast(error.getMessage());
                    }
                });
    }

    private void loadPeer() {
        FirebaseUtil.db().child(Constants.DB_USERS)
                .child(peerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        peerUser = snapshot.getValue(User.class);
                        if (peerUser == null || currentUser == null) {
                            toast(getString(com.mentorlink.app.R.string.unable_to_open_chat));
                            finish();
                            return;
                        }

                        if (currentUser.getRole().equals(peerUser.getRole())) {
                            toast(getString(com.mentorlink.app.R.string.chat_role_restricted));
                            finish();
                            return;
                        }

                        startMessageListener();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        toast(error.getMessage());
                    }
                });
    }

    private void startMessageListener() {
        messages.clear();
        messageIds.clear();
        adapter.notifyDataSetChanged();

        DatabaseReference ref = FirebaseUtil.db()
                .child(Constants.DB_MESSAGES)
                .child(conversationId);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null && !messageIds.contains(message.getId())) {
                    messageIds.add(message.getId());
                    messages.add(message);
                    adapter.notifyItemInserted(messages.size() - 1);
                    binding.recyclerMessages.smoothScrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast(error.getMessage());
            }
        };

        ref.addChildEventListener(childEventListener);
    }

    private void sendMessage() {
        String text = binding.etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text) || currentUser == null || peerUser == null) {
            return;
        }

        DatabaseReference messageRef = FirebaseUtil.db()
                .child(Constants.DB_MESSAGES)
                .child(conversationId)
                .push();

        String messageId = messageRef.getKey();
        if (messageId == null) {
            return;
        }

        Message message = new Message(
                messageId,
                currentUser.getUid(),
                peerUser.getUid(),
                text,
                System.currentTimeMillis()
        );

        binding.etMessage.setText("");

        messageRef.setValue(message)
                .addOnSuccessListener(unused -> updateConversationSummary(text))
                .addOnFailureListener(e -> toast(e.getMessage()));
    }

    private void updateConversationSummary(String text) {
        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);

        if (Constants.ROLE_MENTOR.equals(currentUser.getRole())) {
            conversation.setMentorId(currentUser.getUid());
            conversation.setMentorName(currentUser.getName());
            conversation.setMentorImageUrl(currentUser.getImageUrl());
            conversation.setStudentId(peerUser.getUid());
            conversation.setStudentName(peerUser.getName());
            conversation.setStudentImageUrl(peerUser.getImageUrl());
        } else {
            conversation.setMentorId(peerUser.getUid());
            conversation.setMentorName(peerUser.getName());
            conversation.setMentorImageUrl(peerUser.getImageUrl());
            conversation.setStudentId(currentUser.getUid());
            conversation.setStudentName(currentUser.getName());
            conversation.setStudentImageUrl(currentUser.getImageUrl());
        }

        conversation.setLastMessage(text);
        conversation.setLastUpdated(System.currentTimeMillis());

        FirebaseUtil.db().child(Constants.DB_CONVERSATIONS)
                .child(conversationId)
                .setValue(conversation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (childEventListener != null) {
            FirebaseUtil.db()
                    .child(Constants.DB_MESSAGES)
                    .child(conversationId)
                    .removeEventListener(childEventListener);
        }
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
