package com.mentorlink.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mentorlink.app.databinding.ItemConversationBinding;
import com.mentorlink.app.model.Conversation;
import com.mentorlink.app.util.DateUtil;
import com.mentorlink.app.util.ImageLoader;

import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private final List<Conversation> conversations;
    private final OnConversationClickListener listener;
    private final String currentUid;
    private final String currentRole;

    public InboxAdapter(List<Conversation> conversations, OnConversationClickListener listener, String currentUid, String currentRole) {
        this.conversations = conversations;
        this.listener = listener;
        this.currentUid = currentUid;
        this.currentRole = currentRole;
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConversationBinding binding = ItemConversationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new InboxViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        holder.bind(conversations.get(position));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class InboxViewHolder extends RecyclerView.ViewHolder {
        private final ItemConversationBinding binding;

        InboxViewHolder(ItemConversationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Conversation conversation) {
            String name;
            String imageUrl;

            if ("Mentor".equals(currentRole) && currentUid.equals(conversation.getMentorId())) {
                name = conversation.getStudentName();
                imageUrl = conversation.getStudentImageUrl();
            } else {
                name = conversation.getMentorName();
                imageUrl = conversation.getMentorImageUrl();
            }

            binding.tvName.setText(name);
            binding.tvMessage.setText(conversation.getLastMessage());
            binding.tvTime.setText(DateUtil.format(conversation.getLastUpdated()));
            ImageLoader.load(binding.ivAvatar, imageUrl);
            binding.getRoot().setOnClickListener(v -> listener.onConversationClick(conversation));
        }
    }
}
