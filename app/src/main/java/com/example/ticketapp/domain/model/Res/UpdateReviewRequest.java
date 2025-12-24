package com.example.ticketapp.domain.model.Res;

public class UpdateReviewRequest {
    private String reviewId;
    private String userId;
    private int rating;
    private String comment;

    public UpdateReviewRequest(String reviewId, String userId, int rating, String comment) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
