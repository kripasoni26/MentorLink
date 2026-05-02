package com.mentorlink.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mentorlink.app.databinding.ItemMentorBinding;
import com.mentorlink.app.model.User;
import com.mentorlink.app.util.ImageLoader;

import java.util.List;

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.MentorViewHolder> {

    public interface OnMentorClickListener {
        void onMentorClick(User mentor);
    }

    private final List<User> mentors;
    private final OnMentorClickListener listener;

    public MentorAdapter(List<User> mentors, OnMentorClickListener listener) {
        this.mentors = mentors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MentorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMentorBinding binding = ItemMentorBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MentorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MentorViewHolder holder, int position) {
        holder.bind(mentors.get(position));
    }

    @Override
    public int getItemCount() {
        return mentors.size();
    }

    class MentorViewHolder extends RecyclerView.ViewHolder {
        private final ItemMentorBinding binding;

        MentorViewHolder(ItemMentorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(User mentor) {
            binding.tvName.setText(mentor.getName());
            binding.tvRole.setText(mentor.getRole());
            binding.tvBio.setText(mentor.getBio());
            binding.tvExpertise.setText(mentor.getExpertise());
            ImageLoader.load(binding.ivAvatar, mentor.getImageUrl());
            binding.btnChat.setOnClickListener(v -> listener.onMentorClick(mentor));
        }
    }
}
