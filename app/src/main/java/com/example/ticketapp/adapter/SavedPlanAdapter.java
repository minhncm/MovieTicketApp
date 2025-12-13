package com.example.ticketapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketapp.R;
import com.example.ticketapp.domain.model.SavedPlanEntity;

/**
 * Adapter for displaying saved plans in RecyclerView
 */
public class SavedPlanAdapter extends ListAdapter<SavedPlanEntity, SavedPlanAdapter.PlanViewHolder> {

    private final OnPlanActionListener listener;

    public interface OnPlanActionListener {
        void onCheckout(SavedPlanEntity plan);
        void onDelete(SavedPlanEntity plan);
        void onItemClick(SavedPlanEntity plan);
    }

    public SavedPlanAdapter(OnPlanActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<SavedPlanEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SavedPlanEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull SavedPlanEntity oldItem,
                                               @NonNull SavedPlanEntity newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull SavedPlanEntity oldItem,
                                                  @NonNull SavedPlanEntity newItem) {
                    return oldItem.getMovieTitle().equals(newItem.getMovieTitle()) &&
                            oldItem.getPersonCount() == newItem.getPersonCount() &&
                            (oldItem.getSelectedSeats() == null ? newItem.getSelectedSeats() == null
                                    : oldItem.getSelectedSeats().equals(newItem.getSelectedSeats()));
                }
            };

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_plan , parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPoster;
        private final TextView tvIndex;
        private final TextView tvGenre;
        private final TextView tvTitle;
        private final TextView tvDuration;
        private final TextView tvRating;
        private final TextView tvCinema;
        private final TextView tvDate;
        private final TextView tvTime;
        private final TextView tvSeats;
        private final TextView tvPersonCount;
        private final ImageButton btnMinus;
        private final ImageButton btnPlus;
        private final View btnCheckout;
        private final ImageButton btnDelete;

        PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvCinema = itemView.findViewById(R.id.tvCinema);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvPersonCount = itemView.findViewById(R.id.tvPersonCount);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnCheckout = itemView.findViewById(R.id.btnCheckout);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(SavedPlanEntity plan) {
            int position = getAdapterPosition() + 1;
            tvIndex.setText(position + ". " + (plan.getDate() != null ? plan.getDate() : "No date"));

            tvGenre.setText(plan.getGenre() != null ? plan.getGenre() : "Unknown");
            tvTitle.setText(plan.getMovieTitle() != null ? plan.getMovieTitle() : "Unknown Movie");
            tvDuration.setText(plan.getDuration() != null ? plan.getDuration() : "N/A");
            tvRating.setText(String.valueOf(plan.getRating()));
            tvCinema.setText(plan.getCinemaName() != null ? plan.getCinemaName() : "Select cinema");
            tvDate.setText(plan.getDate() != null ? plan.getDate() : "Select date");
            tvTime.setText(plan.getTime() != null ? plan.getTime() : "Select time");
            tvSeats.setText(plan.getSelectedSeats() != null ? plan.getSelectedSeats() : "Select seats");
            tvPersonCount.setText(String.valueOf(plan.getPersonCount()));

            // Load poster
            if (plan.getMoviePoster() != null && !plan.getMoviePoster().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(plan.getMoviePoster())
                        .placeholder(R.drawable.sample_movie)
                        .error(R.drawable.sample_movie)
                        .into(imgPoster);
            } else {
                imgPoster.setImageResource(R.drawable.sample_movie);
            }

            // Person count buttons
            btnMinus.setOnClickListener(v -> {
                if (plan.getPersonCount() > 1) {
                    plan.setPersonCount(plan.getPersonCount() - 1);
                    tvPersonCount.setText(String.valueOf(plan.getPersonCount()));
                }
            });

            btnPlus.setOnClickListener(v -> {
                plan.setPersonCount(plan.getPersonCount() + 1);
                tvPersonCount.setText(String.valueOf(plan.getPersonCount()));
            });

            // Action buttons
            btnCheckout.setOnClickListener(v -> {
                if (listener != null) listener.onCheckout(plan);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(plan);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(plan);
            });
        }
    }
}
