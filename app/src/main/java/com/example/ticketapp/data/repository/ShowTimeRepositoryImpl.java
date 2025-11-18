package com.example.ticketapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ticketapp.data.network.ApiService;
import com.example.ticketapp.domain.model.Showtimes;
import com.example.ticketapp.domain.repository.ShowTimeRepository;
import com.example.ticketapp.utils.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ShowTimeRepositoryImpl implements ShowTimeRepository {
    private final ApiService apiService;

    @Inject
    public ShowTimeRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Showtimes>>> getShowTimes(String date, String cinemaId,String movieID) {
        MutableLiveData<Resource<List<Showtimes>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());
        apiService.getShowTimes(date, cinemaId,movieID).enqueue(new Callback<List<Showtimes>>() {
            @Override
            public void onResponse(Call<List<Showtimes>> call, Response<List<Showtimes>> response) {
                if (response.isSuccessful()) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    Log.d("CinemaRepositoryImpl", "Response error: " + response.message());
                    data.setValue(Resource.error("Lỗi: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Showtimes>> call, Throwable t) {
                Log.d("CinemaRepositoryImpl", "Response error: " + t.getMessage());

                data.setValue(Resource.error("Thất bại: " + t.getMessage()));
            }
        });

        return data;
    }
}
