package com.example.ticketapp.domain.model;

public class MovieReview {
    private String id;
    private String movieId;
    private String userId;
    private String userName;
    private String userAvatar;
    private float rating; // 1.0 - 5.0
    private String comment;
    private long timestamp;

    public MovieReview() {
    }

    public MovieReview(String id, String movieId, String userId, String userName, 
                       String userAvatar, float rating, String comment, long timestamp) {
        this.id = id;
        this.movieId = movieId;
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
