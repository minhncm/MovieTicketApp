package com.example.ticketapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketapp.domain.model.Cinema;
import com.example.ticketapp.domain.model.Showtimes;
import com.example.ticketapp.domain.repository.CinemaRepository;
import com.example.ticketapp.domain.repository.ShowTimeRepository;
import com.example.ticketapp.utils.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CinemaViewModel extends ViewModel {
    public MutableLiveData<Resource<List<Cinema>>> listAllCinema = new MutableLiveData<>();

    private MutableLiveData<List<Cinema>> listCinema = new MutableLiveData<>();
    public LiveData<List<Cinema>> _listCinema = listCinema;
    private MutableLiveData<String> cityLiveData = new MutableLiveData<>();
    private MediatorLiveData<Resource<List<Cinema>>> cinemasInCityLiveData = new MediatorLiveData<>();
    private LiveData<Resource<List<Cinema>>> currentSource = null;
    private MutableLiveData<String> dateLiveDate = new MutableLiveData<>();
    private MutableLiveData<String> cinemaLiveDate = new MutableLiveData<>();
    private MediatorLiveData<Resource<List<Showtimes>>> showTimes = new MediatorLiveData<>();
    private MutableLiveData<String> movieSelected = new MutableLiveData<>();
    private CinemaRepository repository;
    public void setListCinema(List<Cinema> _list) {
        listCinema.postValue(_list);

    }

    public void setDate(String date) {
        dateLiveDate.setValue(date);
    }

    public void setCinemaID(String cinemaID) {
        cinemaLiveDate.setValue(cinemaID);
    }

    public void setMovieSelected(String movieID) {
        movieSelected.setValue(movieID);
    }

    @Inject
    public CinemaViewModel(CinemaRepository _repository, ShowTimeRepository showTimeRepository) {
        this.repository = _repository;
        cinemasInCityLiveData.addSource(cityLiveData, city -> {
            if (currentSource != null) {
                cinemasInCityLiveData.removeSource(currentSource);
            }
            if (city == null || city.isEmpty()) {
                cinemasInCityLiveData.setValue(Resource.success(new ArrayList<>()));
            } else {
                // Remove source cũ trước khi add source mới
                currentSource = repository.getCinemas(city);
                cinemasInCityLiveData.addSource(currentSource, cinemasInCityLiveData::setValue);
            }
        });

        showTimes.addSource(dateLiveDate, date -> {
            String cinemaID = cinemaLiveDate.getValue();
            String movieID = movieSelected.getValue();
            if (cinemaID != null && !cinemaID.isEmpty() && date != null && !date.isEmpty() &&
                    movieID != null && !movieID.isEmpty()) {
                LiveData<Resource<List<Showtimes>>> newSource = showTimeRepository.getShowTimes(date, cinemaID, movieID);
                showTimes.addSource(newSource, showTimes::setValue);
            }
        });
        showTimes.addSource(cinemaLiveDate, cinemaID -> {
            String date = dateLiveDate.getValue();
            String movieID = movieSelected.getValue();

            if (cinemaID != null && !cinemaID.isEmpty() && date != null && !date.isEmpty()
                    && movieID != null && !movieID.isEmpty()) {
                LiveData<Resource<List<Showtimes>>> newSource = showTimeRepository.getShowTimes(date, cinemaID, movieID);
                showTimes.addSource(newSource, showTimes::setValue);
            }
        });
        showTimes.addSource(movieSelected, movieID -> {;
            String cinemaID = cinemaLiveDate.getValue();
            String date = dateLiveDate.getValue();

            if (cinemaID != null && !cinemaID.isEmpty() && date != null && !date.isEmpty()
                    && movieID != null && !movieID.isEmpty()) {
                LiveData<Resource<List<Showtimes>>> newSource = showTimeRepository.getShowTimes(date, cinemaID, movieID);
                showTimes.addSource(newSource, showTimes::setValue);
            }
        });
        listCinema.postValue(new ArrayList<>());
    }

    public LiveData<Resource<List<Cinema>>> getCinemasByCity() {
        // Repository của bạn đã trả về LiveData, hãy trả về nó
        return cinemasInCityLiveData;
    }

    public LiveData<Resource<List<Showtimes>>> getShowTimes() {
        return showTimes;
    }

    public void setCity(String city) {
        cityLiveData.setValue(city);
    }
    public  MutableLiveData<Resource<List<Cinema>>>  getAllCinema(int limit){
        repository.getAllCinemas(limit).observeForever(resource ->{
            if(resource.getData() != null)
                listAllCinema.postValue(resource);
        }
       );
        return  listAllCinema;

    }
}
