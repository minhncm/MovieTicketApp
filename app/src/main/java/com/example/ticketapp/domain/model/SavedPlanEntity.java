package com.example.ticketapp.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Entity for SavedPlan - stored locally in SQLite database
 */
@Entity(tableName = "saved_plans")
public class SavedPlanEntity {

    @PrimaryKey
    @NonNull
    private String id;

    private String movieId;
    private String movieTitle;
    private String moviePoster;
    private String genre;
    private double rating;
    private String duration;

    private String cinemaId;
    private String cinemaName;
    private String showtimeId;

    private String date;
    private String time;
    private String selectedSeats; // JSON string: "C4,C5,C6"
    private int personCount;

    private long createdAt;
    private long updatedAt;

    public SavedPlanEntity() {
        this.id = java.util.UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getMoviePoster() { return moviePoster; }
    public void setMoviePoster(String moviePoster) { this.moviePoster = moviePoster; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getCinemaId() { return cinemaId; }
    public void setCinemaId(String cinemaId) { this.cinemaId = cinemaId; }

    public String getCinemaName() { return cinemaName; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }

    public String getShowtimeId() { return showtimeId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getSelectedSeats() { return selectedSeats; }
    public void setSelectedSeats(String selectedSeats) { this.selectedSeats = selectedSeats; }

    public int getPersonCount() { return personCount; }
    public void setPersonCount(int personCount) { this.personCount = personCount; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
