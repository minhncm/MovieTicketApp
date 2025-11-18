package com.example.ticketapp.domain.model;

import java.util.List;

public class Ticket {
    private String movieName;
    private String cinemaName;
    private List<String> seatNames;
    private String time;
    private String status;
    private double totalPrice;

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    private String id;
    public Ticket(String movieName, String cinemaName, List<String> seatNames, String time, String status, String id, double totalPrice) {
        this.movieName = movieName;
        this.cinemaName = cinemaName;
        this.seatNames = seatNames;
        this.time = time;
        this.status = status;
        this.id = id;
        this.totalPrice = totalPrice;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
