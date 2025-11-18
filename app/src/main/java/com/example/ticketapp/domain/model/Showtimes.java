package com.example.ticketapp.domain.model;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


public class Showtimes {
    private String id;
    private String movieId;
private  String roomName;
    private String cinemaId;
    private Date startTime;
    private Date endTime;
private List<Seat> seats;

    public Showtimes() {
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    // Constructor đầy đủ để dễ dàng tạo đối tượng trong code
    public Showtimes(String movieId, String cinemaId, String theaterId, Date startTime, Date endTime,
                     List<Seat> seats, String roomName) {
        this.movieId = movieId;
        this.cinemaId = cinemaId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;


this.seats  = seats; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getUid() {
        return id;
    }

    public void setUid(String id) {
        this.id = id;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }


    public LocalDateTime getStartTimeAsLocalDateTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }

    /**
     * Chuyển đổi endTime từ Date sang LocalDateTime
     */
    public LocalDateTime getEndTimeAsLocalDateTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }


}