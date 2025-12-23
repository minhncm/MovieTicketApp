package com.example.ticketapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentCinemaDetailBinding;
import com.example.ticketapp.domain.model.Cinema;
import com.example.ticketapp.viewmodel.CinemaViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CinemaDetailFragment extends Fragment {

    private FragmentCinemaDetailBinding binding;
    private CinemaViewModel cinemaViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCinemaDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        cinemaViewModel = new ViewModelProvider(requireActivity()).get(CinemaViewModel.class);
        
        cinemaViewModel.getSelectedCinema().observe(getViewLifecycleOwner(), cinema -> {
            if (cinema != null) {
                displayCinemaDetails(cinema);
            }
        });
    }

    private void displayCinemaDetails(Cinema cinema) {
        binding.tvCinemaName.setText(cinema.getName());
        binding.tvCinemaRating.setText(String.valueOf(cinema.getRating()));
        
        // Hiển thị địa chỉ
        if (cinema.getAddress() != null && !cinema.getAddress().isEmpty()) {
            binding.tvCinemaAddress.setText(cinema.getAddress());
        } else {
            binding.tvCinemaAddress.setText(getString(R.string.txt_cinema_address_sample));
        }
        
        // Hiển thị số phòng chiếu
        if (cinema.getRooms() != null && !cinema.getRooms().isEmpty()) {
            String roomsText = getString(R.string.txt_cinema_rooms_count, cinema.getRooms().size());
            binding.tvRoomsCount.setText(roomsText);
        } else {
            binding.tvRoomsCount.setText(getString(R.string.txt_cinema_rooms_count, 0));
        }
        
        // Load logo
        Glide.with(requireContext())
                .load(cinema.getLogoUrl())
                .error(R.drawable.img_cinema)
                .into(binding.ivCinemaLogo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
