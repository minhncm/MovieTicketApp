package com.example.ticketapp.domain.model;

import java.util.Objects;

/**
 * Model class for a saved movie/event booking plan.
 */
public class SavedPlan {
    private String id;
    private String date; // Ngày của kế hoạch (ví dụ: "02 November 2025")
    private String movieTitle;
    private String genre;
    private double rating;
    private String cinemaName;
    private String selectedTime;
    private String selectedSeats; // Ví dụ: "C4, C5, C6"
    private int personCount;

    // Constructor rỗng (BẮT BUỘC cho Firestore/Gson)
    public SavedPlan() {
    }

    // Constructor đầy đủ (Để tạo đối tượng trong code)
    public SavedPlan(String date, String movieTitle, String genre) {
        this.id = java.util.UUID.randomUUID().toString(); // Tạo ID ngẫu nhiên
        this.date = date;
        this.movieTitle = movieTitle;
        this.genre = genre;
        this.rating = 5.0;
        this.cinemaName = "EbonyLife";
        this.selectedTime = "01.00 PM";
        this.selectedSeats = "C4, C5, C6";
        this.personCount = 2;
    }

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public double getRating() {
        return rating;
    }

    // ... Thêm các getters/setters khác cho các trường còn lại ...

    public int getPersonCount() {
        return personCount;
    }

    // --- Setters ---
    public void setPersonCount(int personCount) {
        this.personCount = personCount;
    }

    // --- Overrides (Cần thiết cho ListAdapter) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedPlan savedPlan = (SavedPlan) o;
        // So sánh tất cả các trường quan trọng
        return Double.compare(savedPlan.rating, rating) == 0 &&
                personCount == savedPlan.personCount &&
                Objects.equals(id, savedPlan.id) &&
                Objects.equals(movieTitle, savedPlan.movieTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, movieTitle, personCount);
    }
}
