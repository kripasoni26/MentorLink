package com.mentorlink.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mentorlink.app.databinding.ItemMessageIncomingBinding;
import com.mentorlink.app.databinding.ItemMessageOutgoingBinding;
import com.mentorlink.app.model.Message;
import com.mentorlink.app.util.DateUtil;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_OUTGOING = 1;
    private static final int TYPE_INCOMING = 2;

    private final List<Message> messages;
    private final String myUid;

    public MessageAdapter(List<Message> messages, String myUid) {
        this.messages = messages;
        this.myUid = myUid;
    }

    @Override
    public int getItemViewType(int position) {
        return myUid.equals(messages.get(position).getSenderId()) ? TYPE_OUTGOING : TYPE_INCOMING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_OUTGOING) {
            ItemMessageOutgoingBinding binding = ItemMessageOutgoingBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new OutgoingHolder(binding);
        }

        ItemMessageIncomingBinding binding = ItemMessageIncomingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new IncomingHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof OutgoingHolder) {
            ((OutgoingHolder) holder).bind(message);
        } else if (holder instanceof IncomingHolder) {
            ((IncomingHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class OutgoingHolder extends RecyclerView.ViewHolder {
        private final ItemMessageOutgoingBinding binding;

        OutgoingHolder(ItemMessageOutgoingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Message message) {
            binding.tvMessage.setText(message.getText());
            binding.tvTime.setText(DateUtil.format(message.getTimestamp()));
        }
    }

    static class IncomingHolder extends RecyclerView.ViewHolder {
        private final ItemMessageIncomingBinding binding;

        IncomingHolder(ItemMessageIncomingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Message message) {
            binding.tvMessage.setText(message.getText());
            binding.tvTime.setText(DateUtil.format(message.getTimestamp()));
        }
    }
}
