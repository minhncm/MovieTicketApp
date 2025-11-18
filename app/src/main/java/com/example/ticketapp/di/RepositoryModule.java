package com.example.ticketapp.di;

import com.example.ticketapp.data.repository.BookingRepositoryImpl;
import com.example.ticketapp.data.repository.CinemaRepositoryImpl;
import com.example.ticketapp.data.repository.MovieRepositoryImpl;
import com.example.ticketapp.data.repository.ShowTimeRepositoryImpl;
import com.example.ticketapp.domain.repository.BookingRepository;
import com.example.ticketapp.domain.repository.CinemaRepository;
import com.example.ticketapp.domain.repository.MovieRepository;
import com.example.ticketapp.domain.repository.ShowTimeRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @Singleton
    public abstract MovieRepository bindMovieRepository(MovieRepositoryImpl impl);
    @Binds
    @Singleton
    public abstract CinemaRepository bindCinemaRepository(CinemaRepositoryImpl impl);
    @Binds
    @Singleton
    public abstract ShowTimeRepository bindShowTimeRepository(ShowTimeRepositoryImpl impl);
      @Binds
    @Singleton
    public abstract BookingRepository bindBookingRepository(BookingRepositoryImpl impl);


}
