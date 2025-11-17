package com.example.ticketapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketapp.R; // Thay bằng package của bạn
import com.example.ticketapp.domain.model.Seat; // Thay bằng package của bạn
import java.util.ArrayList;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private List<Seat> seatList = new ArrayList<>();
    private OnSeatClickListener seatClickListener;

    // Interface để gửi sự kiện click về Fragment
    public interface OnSeatClickListener {
        void onSeatClick(Seat seat, int position);
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

    // Hàm để cập nhật danh sách ghế
    public void setSeats(List<Seat> newSeats) {
        this.seatList.clear();
        this.seatList.addAll(newSeats);
        notifyDataSetChanged();
    }

    // Lớp ViewHolder
    class SeatViewHolder extends RecyclerView.ViewHolder {
        CheckBox seatCheckBox;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            seatCheckBox = itemView.findViewById(R.id.check_box_seat);
        }

        public void bind(Seat seat, int position) {
            // Hiển thị tên ghế (A1, A2...)
            seatCheckBox.setText(seat.getSeatId());

            // Tự động đổi màu dựa trên trạng thái
            if ("available".equals(seat.getStatus().toString())) {
                seatCheckBox.setEnabled(true); // Cho phép bấm
                seatCheckBox.setChecked(false); // Bỏ chọn (nếu đang được tái sử dụng)
            } else {
                seatCheckBox.setEnabled(false); // Vô hiệu hóa (màu đỏ)
            }

            // Xử lý sự kiện click
            seatCheckBox.setOnClickListener(v -> {
                if (seatClickListener != null) {
                    seatClickListener.onSeatClick(seat, position);
                }
            });
        }
    }
}