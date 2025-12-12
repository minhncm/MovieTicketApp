package com.example.ticketapp.data.db;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import com.example.ticketapp.domain.model.SavedPlanEntity;

public interface SavedPlanDao  {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavedPlanEntity plan);

    @Update
    void update(SavedPlanEntity plan);

    @Delete
    void delete(SavedPlanEntity plan);

}
