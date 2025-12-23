package com.example.ticketapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketapp.domain.model.MovieReview;
import com.example.ticketapp.domain.repository.MovieReviewRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MovieReviewViewModel extends ViewModel {
    
    private final MovieReviewRepository repository;
    private final ExecutorService executorService;
    
    private final MutableLiveData<List<MovieReview>> reviewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Float> averageRatingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> reviewCountLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    
    @Inject
    public MovieReviewViewModel(MovieReviewRepository repository) {
        this.repository = repository;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    public LiveData<List<MovieReview>> getReviewsLiveData() {
        return reviewsLiveData;
    }
    
    public LiveData<Float> getAverageRatingLiveData() {
        return averageRatingLiveData;
    }
    
    public LiveData<Integer> getReviewCountLiveData() {
        return reviewCountLiveData;
    }
    
    public LiveData<Boolean> getOperationSuccessLiveData() {
        return operationSuccessLiveData;
    }
    
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    
    public void loadReviewsByMovie(String movieId) {
        executorService.execute(() -> {
            try {
                List<MovieReview> reviews = repository.getReviewsByMovie(movieId);
                reviewsLiveData.postValue(reviews);
                
                float avgRating = repository.getAverageRating(movieId);
                averageRatingLiveData.postValue(avgRating);
                
                int count = repository.getReviewCount(movieId);
                reviewCountLiveData.postValue(count);
            } catch (Exception e) {
                errorLiveData.postValue(e.getMessage());
            }
        });
    }
    
    public void addReview(MovieReview review) {
        executorService.execute(() -> {
            try {
                boolean success = repository.addReview(review);
                operationSuccessLiveData.postValue(success);
                if (success) {
                    loadReviewsByMovie(review.getMovieId());
                }
            } catch (Exception e) {
                errorLiveData.postValue(e.getMessage());
                operationSuccessLiveData.postValue(false);
            }
        });
    }
    
    public void updateReview(MovieReview review) {
        executorService.execute(() -> {
            try {
                boolean success = repository.updateReview(review);
                operationSuccessLiveData.postValue(success);
                if (success) {
                    loadReviewsByMovie(review.getMovieId());
                }
            } catch (Exception e) {
                errorLiveData.postValue(e.getMessage());
                operationSuccessLiveData.postValue(false);
            }
        });
    }
    
    public void deleteReview(String reviewId, String movieId) {
        executorService.execute(() -> {
            try {
                boolean success = repository.deleteReview(reviewId);
                operationSuccessLiveData.postValue(success);
                if (success) {
                    loadReviewsByMovie(movieId);
                }
            } catch (Exception e) {
                errorLiveData.postValue(e.getMessage());
                operationSuccessLiveData.postValue(false);
            }
        });
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
