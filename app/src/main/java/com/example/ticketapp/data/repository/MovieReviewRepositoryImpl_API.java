package com.example.ticketapp.data.repository;

import com.example.ticketapp.data.network.ApiService;
import com.example.ticketapp.domain.model.MovieReview;
import com.example.ticketapp.domain.model.Res.ReviewRequest;
import com.example.ticketapp.domain.model.Res.ReviewRes;
import com.example.ticketapp.domain.model.Res.UpdateReviewRequest;
import com.example.ticketapp.domain.repository.MovieReviewRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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
            Response<ReviewRes> response = apiService.getReviewsByMovie(movieId).execute();
            if (response.isSuccessful() && response.body() != null) {
                ReviewRes reviewRes = response.body();
                if (reviewRes.isSuccess() && reviewRes.getReviews() != null) {
                    return reviewRes.getReviews();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public MovieReview getUserReviewForMovie(String movieId, String userId) {
        try {
            Response<ReviewRes> response = apiService.getReviewsByUser(userId).execute();
            if (response.isSuccessful() && response.body() != null) {
                ReviewRes reviewRes = response.body();
                if (reviewRes.isSuccess() && reviewRes.getReviews() != null) {
                    for (MovieReview review : reviewRes.getReviews()) {
                        if (review.getMovieId().equals(movieId)) {
                            return review;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addReview(MovieReview review, String bookingId) {
        try {
            ReviewRequest request = new ReviewRequest(
                review.getUserId(),
                review.getMovieId(),
                bookingId,
                (int) review.getRating(),
                review.getComment()
            );
            
            Response<ReviewRes> response = apiService.createReview(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().isSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            
            Response<ReviewRes> response = apiService.updateReview(review).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().isSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteReview(String reviewId, String userId) {
        try {
            Response<ReviewRes> response = apiService.deleteReview(reviewId, userId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().isSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
