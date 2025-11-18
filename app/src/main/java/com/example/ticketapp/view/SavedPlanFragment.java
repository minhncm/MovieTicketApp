package com.example.ticketapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ticketapp.databinding.FragmentSavedPlanBinding;
import com.example.ticketapp.domain.model.SavedPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;


public class SavedPlanFragment extends Fragment  {
    private FragmentSavedPlanBinding biding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        biding = FragmentSavedPlanBinding.inflate(inflater,container,false)        ;
        return  biding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}