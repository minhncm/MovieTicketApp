package com.example.ticketapp.adapter;

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
import com.example.ticketapp.utils.Format;

public class SavedPlanAdapter extends ListAdapter<SavedPlanEntity, SavedPlanAdapter.PlanViewHolder> {

    private final OnPlanActionListener listener;

    public interface OnPlanActionListener {
        void onBookNow(SavedPlanEntity plan);
        void onDelete(SavedPlanEntity plan);
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
                            oldItem.getMovieId().equals(newItem.getMovieId());
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
            // Title
            binding.tvTitle.setText(plan.getMovieTitle() != null ? plan.getMovieTitle() : "Unknown Movie");
            
            // Genre
            binding.tvGenre.setText(plan.getGenre() != null ? plan.getGenre() : "Unknown");
            
            // Rating
            binding.tvRating.setText(String.valueOf(plan.getRating()));
            
            // Duration - format nếu là số
            String duration = plan.getDuration();
            if (duration != null) {
                try {
                    int minutes = Integer.parseInt(duration);
                    binding.tvDuration.setText(Format.formatDuration(minutes));
                } catch (NumberFormatException e) {
                    binding.tvDuration.setText(duration);
                }
            } else {
                binding.tvDuration.setText("N/A");
            }

            // Load poster
            if (plan.getMoviePoster() != null && !plan.getMoviePoster().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(plan.getMoviePoster())
                        .placeholder(R.drawable.sample_movie)
                        .error(R.drawable.sample_movie)
                        .into(binding.imgPoster);
            } else {
                binding.imgPoster.setImageResource(R.drawable.sample_movie);
            }

            // Book Now button
            binding.btnBookNow.setOnClickListener(v -> {
                if (listener != null) listener.onBookNow(plan);
            });

            // Delete button
            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(plan);
            });
        }
    }
}
