package com.example.ticketapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentSavedPlanBinding;
import com.example.ticketapp.domain.model.SavedPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;


public class SavedPlanFragment extends Fragment  {
    private FragmentSavedPlanBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSpinners();
    }

    private void setupSpinners() {
        // Get all spinners from the layout
        View rootView = binding.getRoot();
        setupSpinner(rootView.findViewById(R.id.spinnerCinema));
        setupSpinner(rootView.findViewById(R.id.spinnerDate));
        setupSpinner(rootView.findViewById(R.id.spinnerTime));
        setupSpinner(rootView.findViewById(R.id.spinnerSeats));
    }

    private void setupSpinner(Spinner spinner) {
        if (spinner == null) return;

        // Get data from cities array or use sample data
        String[] items = getResources().getStringArray(R.array.cities_array);

        // Create adapter with custom layouts
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_selected_item,  // Layout when spinner is closed (white text)
                items
        );

        // Set dropdown layout (black text on white background)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply adapter to spinner
        spinner.setAdapter(adapter);
        
        // Set popup background
        spinner.setPopupBackgroundResource(R.drawable.spinner_dropdown_background);
    }
}