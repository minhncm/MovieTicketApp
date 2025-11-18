package com.example.ticketapp.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketapp.R;
import com.example.ticketapp.databinding.ItemMovieHorizontalBinding;
import com.example.ticketapp.domain.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class ExploreMovieAdapter extends RecyclerView.Adapter<ExploreMovieAdapter.MovieViewHolder> {
    private List<Movie> movieList  ;
    private ItemMovieHorizontalBinding binding;
    private com.example.ticketapp.adapter.MovieAdapter.OnItemClickListener onItemClickListener;
    public  ExploreMovieAdapter( ){
        movieList = new ArrayList<>();

    }
    public void setOnItemClickListener(com.example.ticketapp.adapter.MovieAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public void updateListMovie(List<Movie> _movieList){
        if (_movieList != null) {
            this.movieList = _movieList;
        } else {
            this.movieList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = ItemMovieHorizontalBinding.inflate(
                android.view.LayoutInflater.from(parent.getContext()), parent, false
        );
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(movieList.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(movieList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private  ItemMovieHorizontalBinding binding;
        public MovieViewHolder ( ItemMovieHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(Movie movie) {
            if (movie == null) return;

            binding.tvMovieTitle.setText(movie.getTitle());
            Glide.with(binding.getRoot().getContext())
                    .load(movie.getPosterUrl())
                    .error(R.drawable.ic_launcher_background)
                    .into(binding.imgMoviePoster);

        }

    }
    public interface  OnItemClickListener {
        void onItemClick(Movie movie);
    }
}

