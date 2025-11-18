package com.example.ticketapp.domain.model.Res;

import com.example.ticketapp.domain.model.Ticket;

import java.util.List;

public class TicketRes {
    private List<Ticket> bookingsList;

    public List<Ticket> getBookingsList() {
        return bookingsList;
    }

    public void setBookingsList(List<Ticket> bookingsList) {
        this.bookingsList = bookingsList;
    }
}
