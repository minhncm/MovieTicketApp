package com.example.ticketapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketapp.R;
import com.example.ticketapp.domain.model.Seat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private List<Seat> seatList = new ArrayList<>();
    private Set<String> selectedSeatIds = new HashSet<>();
    private OnSeatClickListener seatClickListener;

    public interface OnSeatClickListener {
        void onSeatClick(Seat seat, int position, boolean isSelected);
    }

    public SeatAdapter(OnSeatClickListener listener) {
        this.seatClickListener = listener;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);
        holder.bind(seat, position);
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public void setSeats(List<Seat> newSeats) {
        this.seatList.clear();
        this.seatList.addAll(newSeats);
        this.selectedSeatIds.clear();
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedSeatIds.clear();
        notifyDataSetChanged();
    }

    public List<String> getSelectedSeatIds() {
        return new ArrayList<>(selectedSeatIds);
    }

    class SeatViewHolder extends RecyclerView.ViewHolder {
        CheckBox seatCheckBox;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            seatCheckBox = itemView.findViewById(R.id.check_box_seat);
        }

        public void bind(Seat seat, int position) {
            seatCheckBox.setText(seat.getSeatId());

            boolean isAvailable = "available".equals(seat.getStatus().toString());
            boolean isSelected = selectedSeatIds.contains(seat.getSeatId());

            seatCheckBox.setEnabled(isAvailable);
            
            // Tạm thời remove listener để tránh trigger khi setChecked
            seatCheckBox.setOnCheckedChangeListener(null);
            seatCheckBox.setChecked(isSelected);

            // Set listener sau khi đã set checked state
            seatCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedSeatIds.add(seat.getSeatId());
                } else {
                    selectedSeatIds.remove(seat.getSeatId());
                }
                
                if (seatClickListener != null) {
                    seatClickListener.onSeatClick(seat, position, isChecked);
                }
            });
        }
    }
}
