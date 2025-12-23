package com.example.ticketapp.domain.model.Res;

public class ReviewRequest {
    private String userId;
    private String movieId;
    private String bookingId;
    private int rating;
    private String comment;

    public ReviewRequest(String userId, String movieId, String bookingId, int rating, String comment) {
        this.userId = userId;
        this.movieId = movieId;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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
