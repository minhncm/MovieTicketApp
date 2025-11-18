package com.example.ticketapp.domain.model.Res;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class BookingData {
    private String showtimeId;
    private List<String> selectedSeats;
    private String userId;

    public String getShowTimeId() {
        return showtimeId;
    }
      public void clearData(){
        showtimeId = "";
        selectedSeats = new ArrayList<>();
    }

    public void setShowTimeId(String showTimeId) {
        this.showtimeId = showTimeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<String> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<String> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }
}
