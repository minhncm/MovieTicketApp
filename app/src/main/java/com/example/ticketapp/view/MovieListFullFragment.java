package com.example.ticketapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ticketapp.adapter.MovieAdapter;
import com.example.ticketapp.databinding.FragmentMovieListFullBinding;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.viewmodel.MovieViewModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieListFullFragment extends Fragment {

    private FragmentMovieListFullBinding binding;
    private MovieViewModel movieViewModel;
    private MovieAdapter movieAdapter;
    private String movieType;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMovieListFullBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(MovieListFullFragment.this);
        
        // Lấy arguments từ navigation
        if (getArguments() != null) {
            MovieListFullFragmentArgs args = MovieListFullFragmentArgs.fromBundle(getArguments());
            movieType = args.getMovieType();
        }

        setupViewModel();
        setupRecyclerView();
        updateTitle();
    }

    private void setupRecyclerView() {
        movieAdapter = new MovieAdapter();
        movieAdapter.setOnItemClickListener(this::handleMovieClick);
        
        // Grid layout với 2 cột
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.rvMovieList.setLayoutManager(layoutManager);
        binding.rvMovieList.setAdapter(movieAdapter);
    }

    private void setupViewModel() {
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        
        movieViewModel.movies.observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.rvMovieList.setVisibility(View.GONE);
                    break;
                    
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.rvMovieList.setVisibility(View.VISIBLE);
                    
                    List<Movie> filteredMovies = filterMoviesByType(resource.getData());
                    movieAdapter.updateListMovie(filteredMovies);
                    
                    // Hiển thị message nếu không có phim
                    if (filteredMovies.isEmpty()) {
                        binding.tvEmptyMessage.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvEmptyMessage.setVisibility(View.GONE);
                    }
                    break;
                    
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvEmptyMessage.setVisibility(View.VISIBLE);
                    binding.tvEmptyMessage.setText(resource.getMessage());
                    break;
            }
        });
    }

    private List<Movie> filterMoviesByType(List<Movie> movies) {
        if (movies == null) return List.of();
        
        switch (movieType) {
            case "NOW_SHOWING":
                return movies.stream()
                        .filter(movie -> Objects.equals(movie.getStatus(), "NOW_SHOWING"))
                        .collect(Collectors.toList());
                        
            case "COMING_SOON":
                return movies.stream()
                        .filter(movie -> Objects.equals(movie.getStatus(), "COMING_SOON"))
                        .collect(Collectors.toList());
                        
            default:
                return movies;
        }
    }

    private void updateTitle() {
        String title;
        switch (movieType) {
            case "NOW_SHOWING":
                title = "Phim Được Đề Xuất";
                break;
            case "COMING_SOON":
                title = "Phim Hàng Đầu";
                break;
            default:
                title = "Danh Sách Phim";
                break;
        }
        binding.tvTitle.setText(title);
    }

    private void handleMovieClick(Movie movie) {
        if (movie != null) {
            movieViewModel.setSelectMovie(movie);
            navController.navigate(
                MovieListFullFragmentDirections.actionMovieListFullFragmentToDetailsFragment()
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
