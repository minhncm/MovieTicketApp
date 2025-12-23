package com.example.ticketapp.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketapp.R;
import com.example.ticketapp.databinding.ItemSavedPlanBinding;
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
        ItemSavedPlanBinding binding = ItemSavedPlanBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PlanViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        private final ItemSavedPlanBinding binding;

        PlanViewHolder(@NonNull ItemSavedPlanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SavedPlanEntity plan) {
            int position = getAdapterPosition() + 1;
            binding.tvIndex.setText(position + ". " + (plan.getDate() != null ? plan.getDate() : "No date"));

            binding.tvGenre.setText(plan.getGenre() != null ? plan.getGenre() : "Unknown");
            binding.tvTitle.setText(plan.getMovieTitle() != null ? plan.getMovieTitle() : "Unknown Movie");
            binding.tvDuration.setText(plan.getDuration() != null ? plan.getDuration() : "N/A");
            binding.tvRating.setText(String.valueOf(plan.getRating()));
            binding.tvCinema.setText(plan.getCinemaName() != null ? plan.getCinemaName() : "Select cinema");
            binding.tvDate.setText(plan.getDate() != null ? plan.getDate() : "Select date");
            binding.tvTime.setText(plan.getTime() != null ? plan.getTime() : "Select time");
            binding.tvSeats.setText(plan.getSelectedSeats() != null ? plan.getSelectedSeats() : "Select seats");
            binding.tvPersonCount.setText(String.valueOf(plan.getPersonCount()));

            // Load poster
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                if (plan.getMoviePoster() != null && !plan.getMoviePoster().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(plan.getMoviePoster())
                            .placeholder(R.drawable.sample_movie)
                            .error(R.drawable.sample_movie)
                            .into(binding.imgPoster);
                } else {
                    binding.imgPoster.setImageResource(R.drawable.sample_movie);
                }
            }

            // Person count buttons
            binding.btnMinus.setOnClickListener(v -> {
                if (plan.getPersonCount() > 1) {
                    plan.setPersonCount(plan.getPersonCount() - 1);
                    binding.tvPersonCount.setText(String.valueOf(plan.getPersonCount()));
                }
            });

            binding.btnPlus.setOnClickListener(v -> {
                plan.setPersonCount(plan.getPersonCount() + 1);
                binding.tvPersonCount.setText(String.valueOf(plan.getPersonCount()));
            });

            // Action buttons
            binding.btnCheckout.setOnClickListener(v -> {
                if (listener != null) listener.onCheckout(plan);
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(plan);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(plan);
            });
        }
    }
}
