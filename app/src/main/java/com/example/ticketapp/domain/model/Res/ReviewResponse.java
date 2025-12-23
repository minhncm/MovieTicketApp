package com.example.ticketapp.domain.model.Res;

public class ReviewResponse {
    private String message;
    private String error;

    public ReviewResponse() {
    }

    public ReviewResponse(String message, String error) {
        this.message = message;
        this.error = error;
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

    public boolean isSuccess() {
        return error == null || error.isEmpty();
    }
}
