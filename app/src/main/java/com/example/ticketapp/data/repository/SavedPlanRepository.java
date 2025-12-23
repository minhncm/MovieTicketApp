package com.example.ticketapp.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.ticketapp.data.db.AppDatabase;
import com.example.ticketapp.data.db.SavedPlanDao;
import com.example.ticketapp.domain.model.SavedPlanEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for SavedPlan operations
 */
public class SavedPlanRepository {

    private final SavedPlanDao savedPlanDao;
    private final ExecutorService executorService;

    public SavedPlanRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        savedPlanDao = database.savedPlanDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(SavedPlanEntity plan) {
        executorService.execute(() -> savedPlanDao.insert(plan));
    }

    public void update(SavedPlanEntity plan) {
        plan.setUpdatedAt(System.currentTimeMillis());
        executorService.execute(() -> savedPlanDao.update(plan));
    }

    public void delete(SavedPlanEntity plan) {
        executorService.execute(() -> savedPlanDao.delete(plan));
    }
    public LiveData<List<SavedPlanEntity>> getAllPlans() {
        return savedPlanDao.getAllPlans();
    }
    public LiveData<Integer> getPlansCount() {
        return savedPlanDao.getPlansCount();
    }



}
