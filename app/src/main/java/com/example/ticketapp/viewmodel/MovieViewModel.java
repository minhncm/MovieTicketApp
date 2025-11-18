package com.example.ticketapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.domain.repository.MovieRepository;
import com.example.ticketapp.utils.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MovieViewModel extends ViewModel {
    private final MovieRepository repository;
    private final MutableLiveData<Resource<List<Movie>>> _movies = new MutableLiveData<>();
    public LiveData<Resource<List<Movie>>> movies = _movies;
    private final MutableLiveData<Movie> _selectedMovie = new MutableLiveData<>();
    public LiveData<Movie> selectedMovie = _selectedMovie;
    @Inject
    public MovieViewModel(MovieRepository repository) {
        this.repository = repository;
    }

    public void getMovies() {
        _movies.setValue(Resource.loading());
        repository.getMovies().observeForever(resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                _movies.setValue(Resource.success(resource.getData()));
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                _movies.setValue(Resource.error(resource.getMessage()));
            }
        });
    }
    public  void setSelectMovie(Movie movie) {
        _selectedMovie.setValue(movie);
    }


}
