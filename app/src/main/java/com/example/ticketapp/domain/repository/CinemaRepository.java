package com.example.ticketapp.domain.repository;

import androidx.lifecycle.LiveData;

import com.example.ticketapp.domain.model.Cinema;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.utils.Resource;

import java.util.List;

public interface CinemaRepository {
    LiveData<Resource<List<Cinema>>> getCinemas( String selectedCity);
    LiveData<Resource<List<Cinema>>> getAllCinemas(int limit);


}
