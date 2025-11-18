package com.example.ticketapp.view.Movie;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;

import com.example.ticketapp.adapter.MovieAdapter;
import com.example.ticketapp.databinding.FragmentMovieListBinding;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.utils.CenterItemScrollListener;
import com.example.ticketapp.utils.Resource;
import com.example.ticketapp.view.Ticket.UpcomingTicketsFragment;
import com.example.ticketapp.viewmodel.MovieViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors; // Cần Java 8+


@AndroidEntryPoint
public class MovieListFragment extends Fragment {
 public    interface  OnMovieSelectListener {
        void onMovieSelect(Movie movie);
    }


    private static final String ARG_FILTER_TYPE = "filter_type";

    private FragmentMovieListBinding binding;
    private MovieViewModel movieViewModel;
    private MovieAdapter movieAdapter;
    private  OnMovieSelectListener listener;

    private String filterType; // Ví dụ: "current", "special", "upcoming"

    public static MovieListFragment newInstance(String filterType) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILTER_TYPE, filterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnMovieSelectListener) {
            listener = (OnMovieSelectListener) getParentFragment();
        } else {
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterType = getArguments().getString(ARG_FILTER_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMovieListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movieAdapter = new MovieAdapter();
        movieAdapter.setOnItemClickListener(new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                listener.onMovieSelect(movie);
            }
        });
        binding.rvMovieList.setAdapter(movieAdapter);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.rvMovieList);
        binding.rvMovieList.addOnScrollListener(new CenterItemScrollListener());

        binding.rvMovieList.post(() -> {
            binding.rvMovieList.scrollBy(1, 0);
        });
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        observeMovies();
    }
private  void setUpNavigation(Movie movie){
        movieViewModel.setSelectMovie(movie);
        NavController navController = NavHostFragment.findNavController(MovieListFragment.this);

}
    private void observeMovies() {
        movieViewModel.movies.observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                if (resource.getData() != null) {
                    // Thực hiện lọc dữ liệu và cập nhật Adapter
                    List<Movie> filteredList = filterMovies(resource.getData(), filterType);
                    movieAdapter.updateListMovie(filteredList);
                }
            } else if (resource.getStatus() == Resource.Status.LOADING) {
                // Tùy chọn: Hiển thị loading indicator trong Fragment con
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                Log.e("MovieListFragment", "Error loading movies: " + resource.getMessage());
            }
        });
    }


    private List<Movie> filterMovies(List<Movie> allMovies, String type) {
        if (allMovies == null || type == null) {
            return new ArrayList<>();
        }
        return allMovies.stream()
                .filter(movie -> Objects.equals(movie.getStatus(), type))
                .collect(Collectors.toList());

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}