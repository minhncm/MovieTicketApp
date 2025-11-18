package com.example.ticketapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketapp.databinding.ItemTicketBinding; // Sử dụng View Binding
import com.example.ticketapp.domain.model.Ticket;

import java.util.Locale;

public class TicketAdapter extends ListAdapter<Ticket, TicketAdapter.TicketViewHolder> {
    private ItemTicketBinding binding;
    private final boolean isUpcoming;
    private OnTicketActionListener listener;

    public TicketAdapter(boolean isUpcoming) {
        super(DIFF_CALLBACK);
        this.isUpcoming = isUpcoming;
    }

    public void setOnTicketActionListener(OnTicketActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng View Binding để inflate layout
        binding = ItemTicketBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TicketViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket currentTicket = getItem(position);
        holder.bind(currentTicket);

        // Logic xử lý UI và sự kiện click khác nhau dựa trên isUpcoming
        if (isUpcoming) {
            // Vé Sắp tới (Upcoming)
            holder.binding.textStatusTag.setText(holder.itemView.getContext().getString(com.example.ticketapp.R.string.txt_upcoming));
            // Ẩn nút Đánh giá (Giả sử ID là textRateAction)
            holder.binding.textRateAction.setVisibility(View.GONE);
            binding.getRoot().setOnClickListener(v -> listener.onTicketClick(currentTicket));


        } else {
            // Vé Đã xem (Watched)
            holder.binding.textStatusTag.setText(holder.itemView.getContext().getString(com.example.ticketapp.R.string.txt_status_used));
            holder.binding.textRateAction.setVisibility(View.VISIBLE);
            // Xử lý sự kiện Đánh giá
            binding.getRoot().setOnClickListener(v -> listener.onTicketClick(currentTicket));

            holder.binding.textRateAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRateTicket(currentTicket);
                }
            });
        }
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        // Thay thế findViewById bằng đối tượng binding
        private final ItemTicketBinding binding;

        public TicketViewHolder(@NonNull ItemTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
            });
        }

        public void bind(Ticket ticket) {
            // Sử dụng binding để truy cập các View
            binding.textItemName.setText(ticket.getMovieName());
            binding.textTime.setText(ticket.getTime());
            binding.textLocation.setText(ticket.getCinemaName());
            String formattedPrice = String.format(Locale.getDefault(), "%,d đ", (int) ticket.getTotalPrice());
            binding.textPrice.setText(formattedPrice);}
    }

    // Interface để xử lý các hành động
    public interface OnTicketActionListener {
        void onCancelTicket(Ticket ticket);

        void onRateTicket(Ticket ticket);

        void onTicketClick(Ticket ticket);
    }

    // ===============================================
    // DIFFUTIL CALLBACK
    // ===============================================

    private static final DiffUtil.ItemCallback<Ticket> DIFF_CALLBACK = new DiffUtil.ItemCallback<Ticket>() {
        @Override
        public boolean areItemsTheSame(@NonNull Ticket oldItem, @NonNull Ticket newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Ticket oldItem, @NonNull Ticket newItem) {
            return oldItem.equals(newItem);
        }
    };
}