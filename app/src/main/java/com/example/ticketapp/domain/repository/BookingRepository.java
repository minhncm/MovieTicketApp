package com.example.ticketapp.domain.repository;

import androidx.lifecycle.LiveData;

import com.example.ticketapp.domain.model.Res.BookingData;
import com.example.ticketapp.domain.model.Res.BookingRes;
import com.example.ticketapp.domain.model.Showtimes;
import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.utils.Resource;

import java.util.List;

public interface BookingRepository {
    LiveData<Resource<BookingRes>> bookingTicket(BookingData bookingData);
    LiveData<Resource<List<Ticket>>> fetchTickets(String type,String userId);

}
