package com.example.ticketapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ticketapp.data.network.ApiService;
import com.example.ticketapp.domain.model.Cinema;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.domain.model.Res.CinemaRes;
import com.example.ticketapp.domain.repository.CinemaRepository;
import com.example.ticketapp.utils.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@Singleton
public class CinemaRepositoryImpl implements CinemaRepository {
    private final ApiService apiService;
@Inject
    public CinemaRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public LiveData<Resource<List<Cinema>>> getCinemas(String selectedCity) {
        MutableLiveData<Resource<List<Cinema>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getCinemas(selectedCity).enqueue(new Callback<CinemaRes>() {
            @Override
            public void onResponse(@NonNull Call<CinemaRes> call, @NonNull Response<CinemaRes> response) {
                if (response.isSuccessful()) {

                    CinemaRes res = response.body();
                    assert res != null;
                    data.setValue(Resource.success(res.getCinemas()));
                } else {
                    Log.d("CinemaRepositoryImpl", "Response error: " + response.message());
                    data.setValue(Resource.error("Lỗi: " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CinemaRes> call, @NonNull Throwable t) {
                Log.d("CinemaRepositoryImpl", "Response error: " + t.getMessage());

                data.setValue(Resource.error("Thất bại: " + t.getMessage()));
            }
        });

        return data;
    }

    @Override
    public LiveData<Resource<List<Cinema>>> getAllCinemas(int limit) {
        MutableLiveData<Resource<List<Cinema>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getAllCinemas(limit).enqueue(new Callback<CinemaRes>() {
            @Override
            public void onResponse(@NonNull Call<CinemaRes> call, @NonNull Response<CinemaRes> response) {
                if (response.isSuccessful()) {
                    CinemaRes res = response.body();
                    assert res != null;
                    data.setValue(Resource.success(res.getCinemas()));
                } else {
                    Log.d("CinemaRepositoryImpl", "Response error: " + response.message());
                    data.setValue(Resource.error("Lỗi: " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CinemaRes> call, @NonNull Throwable t) {
                Log.d("CinemaRepositoryImpl", "Response error: " + t.getMessage());

                data.setValue(Resource.error("Thất bại: " + t.getMessage()));
            }
        });

        return data;    }


}
