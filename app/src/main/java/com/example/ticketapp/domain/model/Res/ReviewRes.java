package com.example.ticketapp.domain.model.Res;

import com.example.ticketapp.domain.model.MovieReview;
import java.util.List;

public class ReviewRes {
    private String message;
    private String error;
    private MovieReview review;
    private List<MovieReview> reviews;

    public ReviewRes() {
    }

    public ReviewRes(String message, String error, MovieReview review) {
        this.message = message;
        this.error = error;
        this.review = review;
    }

    public ReviewRes(String message, String error, List<MovieReview> reviews) {
        this.message = message;
        this.error = error;
        this.reviews = reviews;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public MovieReview getReview() {
        return review;
    }

    public void setReview(MovieReview review) {
        this.review = review;
    }

    public List<MovieReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
    }

    public boolean isSuccess() {
        return error == null || error.isEmpty();
    }
}
