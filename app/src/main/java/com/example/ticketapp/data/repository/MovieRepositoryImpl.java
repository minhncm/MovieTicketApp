package com.example.ticketapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ticketapp.data.network.ApiService;
import com.example.ticketapp.domain.model.Cinema;
import com.example.ticketapp.domain.model.Res.BookingData;
import com.example.ticketapp.domain.model.Res.BookingRes;
import com.example.ticketapp.domain.model.Res.TicketRes;
import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.domain.repository.BookingRepository;
import com.example.ticketapp.utils.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class BookingRepositoryImpl implements BookingRepository {
    private  final ApiService apiService;
    @Inject
    public BookingRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<BookingRes>> bookingTicket(BookingData bookingData) {
        MutableLiveData<Resource<BookingRes>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.bookTicket(bookingData).enqueue(new Callback<BookingRes>() {
            @Override
            public void onResponse(Call<BookingRes> call, Response<BookingRes> response) {
                if (response.isSuccessful()) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    Log.d("BookingRepositoryImpl", "Response error: " + response.message());

                    data.setValue(Resource.error("Lỗi: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<BookingRes> call, Throwable t) {
                Log.d("BookingRepositoryImpl", "Response error: " + t.getMessage());

                data.setValue(Resource.error("Thất bại: " + t.getMessage()));
            }
        });

        return data;
    }

    @Override
    public LiveData<Resource<List<Ticket>>> fetchTickets(String type,String userId) {

        MutableLiveData<Resource<List<Ticket>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.fetchTickets(type,userId).enqueue(new Callback<TicketRes>() {
            @Override
            public void onResponse(Call<TicketRes> call, Response<TicketRes> response) {
                if (response.isSuccessful()) {
                    TicketRes bookingResponse = response.body();
                    if (bookingResponse != null)
                        data.setValue(Resource.success(bookingResponse.getBookingsList()));
                } else {
                    data.setValue(Resource.error("Lỗi: " + response.message()));
                    Log.d("BookingRepositoryImpl", "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<TicketRes> call, Throwable t) {
                data.setValue(Resource.error("Thất bại: " + t.getMessage()));
                Log.d("BookingRepositoryImpl", "Response error: " + t.getMessage());

            }
        });

        return data;
    }
}

