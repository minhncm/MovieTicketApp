package com.example.ticketapp.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ticketapp.domain.model.SavedPlanEntity;

import java.util.List;
@Dao
public interface SavedPlanDao  {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavedPlanEntity plan);

    @Update
    void update(SavedPlanEntity plan);

    @Delete
    void delete(SavedPlanEntity plan);

    @Query("DELETE FROM saved_plans WHERE movieId = :movieId")
    void deleteByMovieId(String movieId);

    @Query("SELECT * FROM saved_plans ORDER BY createdAt DESC")
    LiveData<List<SavedPlanEntity>> getAllPlans();

    @Query("SELECT * FROM saved_plans WHERE id = :planId")
    LiveData<SavedPlanEntity> getPlanById(String planId);
    @Query("SELECT COUNT(*) FROM saved_plans")
    LiveData<Integer> getPlansCount();
}
