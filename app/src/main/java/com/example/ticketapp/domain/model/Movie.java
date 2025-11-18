package com.example.ticketapp.domain.model;

import com.example.ticketapp.domain.model.Res.FirestoreTimestamp;

import java.util.List;

public class Movie {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String title;
    private String director;
    private double rating;
    private List<String> genres;
    private String duration;
    private String synopsis;
    private String posterUrl;
    private String status;
    private FirestoreTimestamp releaseDate;

    public Movie(String id,String title, String director, double rating,
                 List<String> genres, String duration,
                 String synopsis, String posterUrl,String status,FirestoreTimestamp releaseDate) {
        this.title = title;
        this.director = director;
        this.rating = rating;
        this.genres = genres;
        this.duration = duration;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.status = status;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FirestoreTimestamp getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(FirestoreTimestamp releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public double getRating() {
        return rating;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getDuration() {
        return duration;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}

