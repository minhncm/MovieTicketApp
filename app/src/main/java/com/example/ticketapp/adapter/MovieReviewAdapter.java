package com.example.ticketapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketapp.R;
import com.example.ticketapp.domain.model.MovieReview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder> {
    
    private List<MovieReview> reviews;
    private OnReviewClickListener listener;
    
    public interface OnReviewClickListener {
        void onReviewClick(MovieReview review);
        void onReviewLongClick(MovieReview review);
    }
    
    public MovieReviewAdapter() {
        this.reviews = new ArrayList<>();
    }
    
    public void setReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
    
    public void setOnReviewClickListener(OnReviewClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        MovieReview review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private TextView tvComment;
        private TextView tvTimestamp;
        private RatingBar ratingBar;
        
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onReviewClick(reviews.get(getAdapterPosition()));
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onReviewLongClick(reviews.get(getAdapterPosition()));
                    return true;
                }
                return false;
            });
        }
        
        public void bind(MovieReview review) {
            tvUserName.setText(review.getUserName());
            tvComment.setText(review.getComment());
            ratingBar.setRating(review.getRating());
            
            // Format timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(new Date(review.getTimestamp()));
            tvTimestamp.setText(formattedDate);
        }
    }
}
