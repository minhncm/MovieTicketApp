package com.example.ticketapp.view.Movie;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentDetailsBinding;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.domain.model.SavedPlanEntity;
import com.example.ticketapp.utils.Format;
import com.example.ticketapp.viewmodel.MovieViewModel;
import com.example.ticketapp.viewmodel.SavedPlanViewModel;


public class DetailsFragment extends Fragment {
    private FragmentDetailsBinding binding;
    private MovieViewModel movieViewModel;
    private SavedPlanViewModel savedPlanViewModel;
    private boolean isFavorite = false;
    private Movie currentMovie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        savedPlanViewModel = new ViewModelProvider(this).get(SavedPlanViewModel.class);

        return binding.getRoot();
    }

    private void initView(Movie movie) {
        binding.tvTitle.setText(movie.getTitle());
        binding.tvDirector.setText(getString(R.string.txt_director_format, movie.getDirector()));
        for ( String genre : movie.getGenres()) {
            TextView genreView = new TextView(binding.getRoot().getContext());

            // Thiết lập các thuộc tính giống như trong XML của bạn
            genreView.setText(genre);
            genreView.setBackgroundResource(R.drawable.genre_bg); // Giống android:background
            genreView.setTextColor(Color.WHITE); // Giống android:textColor="#FFFFFF"
            genreView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // Giống android:textSize="12sp"

            // Chuyển đổi dp sang pixel để set padding và margin
            Context context = binding.getRoot().getContext();
            int paddingHorizontal = (int) (12 * context.getResources().getDisplayMetrics().density);
            int paddingVertical = (int) (4 * context.getResources().getDisplayMetrics().density);
            int marginEnd = (int) (8 * context.getResources().getDisplayMetrics().density);

            genreView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

            // Thiết lập LayoutParams để có width, height, và margin
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd(marginEnd);
            genreView.setLayoutParams(params);

            // Thêm TextView vừa tạo vào LinearLayout container
            binding.genreContainer.addView(genreView);
        }

        binding.tvRating.setText(String.valueOf(movie.getRating()));
        binding.tvDutation.setText(Format.formatDuration(Integer.parseInt(movie.getDuration())));
        binding.tvSynopsis.setText(movie.getSynopsis());
        Glide.with(requireContext())
                .load(movie.getPosterUrl())
                .error(R.drawable.ic_launcher_background)
                .into(binding.imgPoster);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movieViewModel.selectedMovie.observe(getViewLifecycleOwner(), movie -> {
            if (movie != null) {
                currentMovie = movie;
                initView(movie);
                checkIfFavorite(movie.getId());
            }
        });
        
        binding.btnFavorite.setOnClickListener(v -> toggleFavorite());
        
        binding.btnBookTicket.setOnClickListener(v->{
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(DetailsFragmentDirections.actionDetailsFragmentToSelectSeatFragment());
        });
        
        // Tìm nút View Reviews bằng findViewById
        View btnViewReviews = view.findViewById(R.id.btnViewReviews);
        if (btnViewReviews != null) {
            btnViewReviews.setOnClickListener(v -> {
                try {
                    if (movieViewModel.selectedMovie.getValue() != null) {
                        // Mở MovieReviewFragment (movieId sẽ lấy từ MovieViewModel)
                        NavController navController = NavHostFragment.findNavController(this);
                        navController.navigate(DetailsFragmentDirections.actionDetailsFragmentToMovieReviewFragment());
                    } else {
                        Toast.makeText(getContext(), "Vui lòng chọn phim trước", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            });
        }
    }

    private void checkIfFavorite(String movieId) {
        savedPlanViewModel.getAllPlans().observe(getViewLifecycleOwner(), plans -> {
            if (plans != null) {
                isFavorite = false;
                for (SavedPlanEntity plan : plans) {
                    if (plan.getMovieId() != null && plan.getMovieId().equals(movieId)) {
                        isFavorite = true;
                        break;
                    }
                }
                updateFavoriteIcon();
            }
        });
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void toggleFavorite() {
        if (currentMovie == null) return;

        if (isFavorite) {
            // Xóa khỏi favorites
            savedPlanViewModel.deleteByMovieId(currentMovie.getId());
            isFavorite = false;
            Toast.makeText(requireContext(), R.string.txt_removed_from_favorites, Toast.LENGTH_SHORT).show();
        } else {
            // Thêm vào favorites
            SavedPlanEntity plan = new SavedPlanEntity();
            plan.setMovieId(currentMovie.getId());
            plan.setMovieTitle(currentMovie.getTitle());
            plan.setMoviePoster(currentMovie.getPosterUrl());
            plan.setGenre(currentMovie.getGenres() != null ? String.join(", ", currentMovie.getGenres()) : "");
            plan.setRating(currentMovie.getRating());
            plan.setDuration(currentMovie.getDuration());
            plan.setPersonCount(1);
            
            savedPlanViewModel.insert(plan);
            isFavorite = true;
            Toast.makeText(requireContext(), R.string.txt_added_to_favorites, Toast.LENGTH_SHORT).show();
        }
        updateFavoriteIcon();
    }

}