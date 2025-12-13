package com.example.ticketapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.ticketapp.domain.model.SavedPlanEntity;
import com.example.ticketapp.data.repository.SavedPlanRepository;

import java.util.List;

/**
 * ViewModel for SavedPlan operations
 */
public class SavedPlanViewModel extends AndroidViewModel {

    private final SavedPlanRepository repository;


    public SavedPlanViewModel(@NonNull Application application) {
        super(application);
        repository = new SavedPlanRepository(application);

    }

    public void insert(SavedPlanEntity plan) {
        repository.insert(plan);
    }

    public void update(SavedPlanEntity plan) {
        repository.update(plan);
    }

    public void delete(SavedPlanEntity plan) {
        repository.delete(plan);
    }




    public SavedPlanEntity createPlan(String movieId, String movieTitle, String moviePoster,
                                      String genre, double rating, String duration) {
        SavedPlanEntity plan = new SavedPlanEntity();
        plan.setMovieId(movieId);
        plan.setMovieTitle(movieTitle);
        plan.setMoviePoster(moviePoster);
        plan.setGenre(genre);
        plan.setRating(rating);
        plan.setDuration(duration);
        plan.setPersonCount(1);
        return plan;
    }
}
