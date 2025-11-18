package com.example.ticketapp.domain.model;

public class Seat {
    private String seatId; // <-- Trường mới
    private Double price;
    private SeatStatus status;
    private  SeatType type;
    public Seat(Double price, SeatStatus status, SeatType type, String seatId) {
        this.price = price;
        this.status = status;
        this.type = type;
        this.seatId = seatId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public SeatType getType() {
        return type;
    }

    public void setType(SeatType type) {
        this.type = type;
    }

}
