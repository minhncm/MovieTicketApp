package com.example.ticketapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketapp.data.repository.AccountRepository;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.domain.model.Res.BookingData;
import com.example.ticketapp.domain.model.Res.BookingRes;
import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.domain.repository.BookingRepository;
import com.example.ticketapp.utils.Resource;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BookingViewModel extends ViewModel {
    private final BookingRepository bookingRepository;
    private  final AccountRepository accountRepository;
    private final LiveData<Account> userProfileLiveData;
    private final MediatorLiveData<Resource<List<Ticket>>> ticketsUpcomingResultLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<Ticket>>> ticketsWatchedResultLiveData = new MediatorLiveData<>();

    private MutableLiveData<Ticket> currentTicketLiveData = new MutableLiveData<>();
    public LiveData<Ticket> _currentTicketLiveData = currentTicketLiveData;
    public MutableLiveData<BookingData> bookingDataMutableLiveData = new MutableLiveData<>();



    @Inject
    public BookingViewModel(BookingRepository bookingRepository1, AccountRepository accountRepository1) {
        this.bookingRepository = bookingRepository1;
        this.accountRepository = accountRepository1;
        userProfileLiveData = accountRepository.getCurrentUser();
        ticketsUpcomingResultLiveData.addSource(userProfileLiveData, user -> {
            if (user != null && user.getUid() != null) {
                // Khi UID có sẵn, kích hoạt việc lấy dữ liệu vé
                fetchTicketsByUid(user.getUid(),"upcoming");
            } else {
                // Xử lý trường hợp Logout
                ticketsUpcomingResultLiveData.setValue(Resource.error("Yêu cầu đăng nhập"));
            }
        });
        ticketsWatchedResultLiveData.addSource(userProfileLiveData, user -> {
            if (user != null && user.getUid() != null) {
                // Khi UID có sẵn, kích hoạt việc lấy dữ liệu vé
                fetchTicketsByUid(user.getUid(),"watched");
            } else {
                // Xử lý trường hợp Logout
                ticketsUpcomingResultLiveData.setValue(Resource.error("Yêu cầu đăng nhập"));
            }
        });
    }
    private void fetchTicketsByUid(String userId,String type) {
        if(Objects.equals(type, "upcoming"))
         bookingRepository.fetchTickets(type, userId).observeForever
                (ticketsUpcomingResultLiveData::setValue);
        else {
            bookingRepository.fetchTickets(type, userId).observeForever
                    (ticketsWatchedResultLiveData::setValue);
        }
    }
    public LiveData<Resource<List<Ticket>>> getUpcomingTickets() {
        return ticketsUpcomingResultLiveData;
    }
    public LiveData<Resource<List<Ticket>>> getWatchedTickets() {
        return ticketsWatchedResultLiveData;
    }

    public void setBookingData(BookingData bookingData) {
        bookingDataMutableLiveData.postValue(bookingData);
    }

    public void setCurrentTicket(Ticket ticket) {
        currentTicketLiveData.postValue(ticket);
    }

    public LiveData<Resource<BookingRes>> bookingTicket(BookingData bookingData) {
          return bookingRepository.bookingTicket(bookingData);

    }

}
