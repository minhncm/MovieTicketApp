package com.example.ticketapp.domain.repository;

import androidx.lifecycle.LiveData;

import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.utils.Resource;

import java.util.List;

import javax.inject.Singleton;


public interface MovieRepository {
    LiveData<Resource<List<Movie>>> getMovies();
}