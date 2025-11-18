package com.example.ticketapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ticketapp.R;
import com.example.ticketapp.adapter.ExploreMovieAdapter;
import com.example.ticketapp.adapter.MovieAdapter;
import com.example.ticketapp.databinding.FragmentExploreBinding;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.viewmodel.MovieViewModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ExploreFragment extends Fragment {
    private FragmentExploreBinding binding;
    private RecyclerView nowShowingList;
    private RecyclerView upComingList;
    private ExploreMovieAdapter upComingAdapter;
    private ExploreMovieAdapter nowShowingAdapter;
    private MovieViewModel movieViewModel;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        initView();
        setUpViewModel();
        return binding.getRoot();
    }

    private void setUpViewModel() {
        movieViewModel.movies.observe(getViewLifecycleOwner(),movie ->{
            switch (movie.getStatus())
            {
                case LOADING:

                    break;
                case SUCCESS:
                    List<Movie> upComingMovie  = movie.getData().stream().filter
                            (movie1 -> Objects.equals(movie1.getStatus(), "COMING_SOON"))
                            .collect(Collectors.toList());
                    List<Movie> nowShowingMovie  = movie.getData().stream().filter
                            (movie1 -> Objects.equals(movie1.getStatus(), "NOW_SHOWING"))
                            .collect(Collectors.toList());
                    nowShowingAdapter.updateListMovie(nowShowingMovie);
                    upComingAdapter.updateListMovie(upComingMovie);

                    break;
                case ERROR:

                    break;
            }


        });

    }

    private void initView() {
        nowShowingAdapter = new ExploreMovieAdapter();
        nowShowingAdapter.setOnItemClickListener(new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                handleClick(movie);
            }
        });
        upComingAdapter = new ExploreMovieAdapter();
        upComingAdapter.setOnItemClickListener(new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                handleClick(movie);
            }
        });
        nowShowingList= binding.rvRecommended;
        upComingList = binding.rvTopMovies;
        nowShowingList.setAdapter(nowShowingAdapter);
        upComingList.setAdapter(upComingAdapter);
    }
  private  void handleClick(Movie movie)
  {
      movieViewModel.setSelectMovie(movie);
      NavController navController = NavHostFragment.findNavController(ExploreFragment.this);
      navController.navigate(ExploreFragmentDirections.actionNavDiscoveryToDetailsFragment());
  }
}
