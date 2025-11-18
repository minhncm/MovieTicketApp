package com.example.ticketapp.domain.model.Res;

import java.util.List;

public class BookingRes {
    private String id;
    private  String movieName;
    private String cinemaName;
    private List<String> seatNames;
    private String showStartTime;
    private String status;
    private String message;
    private double totalPrice;

    public BookingRes(String id, String movieName, String cinemaName, List<String> seatNames,
                      String showStartTime, String status, String message, double totalPrice) {
        this.id = id;
        this.movieName = movieName;
        this.cinemaName = cinemaName;
        this.seatNames = seatNames;
        this.showStartTime = showStartTime;
        this.status = status;
        this.message = message;
        this.totalPrice = totalPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public List<String> getSeatNames() {
        return seatNames;
    }

    public void setSeatNames(List<String> seatNames) {
        this.seatNames = seatNames;
    }

    public String getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(String showStartTime) {
        this.showStartTime = showStartTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
