package com.example.ticketapp.data.repository;

import com.example.ticketapp.data.network.ApiService;
import com.example.ticketapp.domain.model.MovieReview;
import com.example.ticketapp.domain.model.Res.ReviewRequest;
import com.example.ticketapp.domain.model.Res.UpdateReviewRequest;
import com.example.ticketapp.domain.repository.MovieReviewRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class MovieReviewRepositoryImpl_API implements MovieReviewRepository {
    
    private final ApiService apiService;
    
    @Inject
    public MovieReviewRepositoryImpl_API(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public List<MovieReview> getReviewsByMovie(String movieId) {
        try {
            Response<List<MovieReview>> response = apiService.getReviewsByMovie(movieId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public MovieReview getUserReviewForMovie(String movieId, String userId) {
        try {
            Response<List<MovieReview>> response = apiService.getReviewsByUser(userId).execute();
            if (response.isSuccessful() && response.body() != null) {
                for (MovieReview review : response.body()) {
                    if (review.getMovieId().equals(movieId)) {
                        return review;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addReview(MovieReview review) {
        try {
            ReviewRequest request = new ReviewRequest(
                review.getUserId(),
                review.getMovieId(),
                "", // bookingId - cần truyền từ ngoài vào
                (int) review.getRating(),
                review.getComment()
            );
            
            Response<MovieReview> response = apiService.createReview(request).execute();
            return response.isSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateReview(MovieReview review) {
        try {
            UpdateReviewRequest request = new UpdateReviewRequest(
                review.getId(),
                review.getUserId(),
                (int) review.getRating(),
                review.getComment()
            );
            
            Response<MovieReview> response = apiService.updateReview(review).execute();
            return response.isSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteReview(String reviewId) {
        try {
            // Cần userId - có thể lấy từ Firebase Auth
            Response<Void> response = apiService.deleteReview(reviewId, "").execute();
            return response.isSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public float getAverageRating(String movieId) {
        List<MovieReview> reviews = getReviewsByMovie(movieId);
        if (reviews.isEmpty()) {
            return 0f;
        }
        
        float sum = 0;
        for (MovieReview review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }

    @Override
    public int getReviewCount(String movieId) {
        return getReviewsByMovie(movieId).size();
    }
}
