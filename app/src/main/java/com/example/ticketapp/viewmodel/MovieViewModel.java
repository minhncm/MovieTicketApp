package com.example.ticketapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.domain.repository.MovieRepository;
import com.example.ticketapp.utils.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MovieViewModel extends ViewModel {
    private final MovieRepository repository;
    private final MutableLiveData<Resource<List<Movie>>> _movies = new MutableLiveData<>();
    public LiveData<Resource<List<Movie>>> movies = _movies;
    private final MutableLiveData<Movie> _selectedMovie = new MutableLiveData<>();
    public LiveData<Movie> selectedMovie = _selectedMovie;
    
    // LiveData cho kết quả tìm kiếm
    private final MutableLiveData<List<Movie>> _searchResults = new MutableLiveData<>();
    public LiveData<List<Movie>> searchResults = _searchResults;
    
    // LiveData cho query tìm kiếm (để giữ text trong SearchResultFragment)
    private final MutableLiveData<String> _searchQuery = new MutableLiveData<>();
    public LiveData<String> searchQuery = _searchQuery;
    
    // Lưu danh sách phim gốc để search
    private List<Movie> allMovies = new ArrayList<>();
    
    // ExecutorService để chạy tìm kiếm trên background thread
    private final ExecutorService searchExecutor = Executors.newSingleThreadExecutor();
    
    @Inject
    public MovieViewModel(MovieRepository repository) {
        this.repository = repository;
    }

    public void getMovies() {
        _movies.setValue(Resource.loading());
        repository.getMovies().observeForever(resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                allMovies = resource.getData() != null ? resource.getData() : new ArrayList<>();
                _movies.setValue(Resource.success(resource.getData()));
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                _movies.setValue(Resource.error(resource.getMessage()));
            }
        });
    }
    
    public void setSelectMovie(Movie movie) {
        _selectedMovie.setValue(movie);
    }
    
    public void searchMovies(String query) {
        // Lưu query để hiển thị trong SearchResultFragment
        _searchQuery.setValue(query);
        
        if (query == null || query.trim().isEmpty()) {
            _searchResults.setValue(new ArrayList<>());
            return;
        }
        
        // Chạy tìm kiếm trên background thread để tránh block UI
        searchExecutor.execute(() -> {
            String searchQuery = query.toLowerCase().trim();
            List<Movie> results = new ArrayList<>();
            
            for (Movie movie : allMovies) {
                boolean found = false;
                
                // Tìm theo tên phim (có dấu và không dấu)
                if (movie.getTitle() != null) {
                    String title = movie.getTitle().toLowerCase();
                    // Tìm kiếm thông thường
                    if (title.contains(searchQuery)) {
                        results.add(movie);
                        continue;
                    }
                    // Tìm kiếm không dấu (tiếng Việt)
                    if (com.example.ticketapp.utils.VietnameseUtils.containsIgnoreAccents(movie.getTitle(), searchQuery)) {
                        results.add(movie);
                        continue;
                    }
                }
                
                // Tìm theo đạo diễn (có dấu và không dấu)
                if (movie.getDirector() != null) {
                    String director = movie.getDirector().toLowerCase();
                    if (director.contains(searchQuery)) {
                        results.add(movie);
                        continue;
                    }
                    if (com.example.ticketapp.utils.VietnameseUtils.containsIgnoreAccents(movie.getDirector(), searchQuery)) {
                        results.add(movie);
                        continue;
                    }
                }
                
                // Tìm theo thể loại (có dấu và không dấu)
                if (movie.getGenres() != null) {
                    for (String genre : movie.getGenres()) {
                        if (genre.toLowerCase().contains(searchQuery)) {
                            results.add(movie);
                            found = true;
                            break;
                        }
                        if (com.example.ticketapp.utils.VietnameseUtils.containsIgnoreAccents(genre, searchQuery)) {
                            results.add(movie);
                            found = true;
                            break;
                        }
                    }
                }
            }
            
            // Post kết quả về UI thread
            _searchResults.postValue(results);
        });
    }
    
    // Clear kết quả tìm kiếm
    public void clearSearch() {
        _searchResults.setValue(new ArrayList<>());
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Shutdown executor khi ViewModel bị destroy
        searchExecutor.shutdown();
    }
}
