package com.example.ticketapp.domain.repository;

import androidx.lifecycle.LiveData;

import com.example.ticketapp.domain.model.Showtimes;
import com.example.ticketapp.utils.Resource;

import java.util.List;

public interface ShowTimeRepository {
    LiveData<Resource<List<Showtimes>>> getShowTimes(String date , String cinemaID,String movieID);

}
