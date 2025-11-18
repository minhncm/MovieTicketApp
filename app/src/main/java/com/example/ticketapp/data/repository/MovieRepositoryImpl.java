package com.example.ticketapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ticketapp.data.network.ApiService;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.domain.repository.MovieRepository;
import com.example.ticketapp.utils.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@Singleton
public class MovieRepositoryImpl implements MovieRepository {
    private final ApiService apiService;

    @Inject
    public MovieRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Resource<List<Movie>>> getMovies() {
        MutableLiveData<Resource<List<Movie>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading());

        apiService.getMovies().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful()) {
                    data.setValue(Resource.success(response.body()));
                } else {
                    Log.d("MovieRepositoryImpl", "Response error: " + response.message());
                    data.setValue(Resource.error("Lỗi: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Log.d("MovieRepositoryImpl", "Response error: " + t.getMessage());

                data.setValue(Resource.error("Thất bại: " + t.getMessage()));
            }
        });

        return data;
    }
}
