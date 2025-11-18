package com.example.ticketapp.view.Ticket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ticketapp.databinding.FragmentETicketBinding;
import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.viewmodel.BookingViewModel;


public class ETicket extends Fragment {
    private FragmentETicketBinding binding;
    private BookingViewModel bookingViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentETicketBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpViewModel();
    }

    private void setUpViewModel() {
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        bookingViewModel._currentTicketLiveData.observe(getViewLifecycleOwner(), ticket -> {
            if (ticket != null) {
                initView(ticket);
            }
        });
    }

    private void initView(Ticket ticket) {
        binding.textFilmTitle.setText(ticket.getMovieName());
        binding.detailLocation.textDetailValue.setText(ticket.getCinemaName());
        binding.detailOrder.textDetailValue.setText(ticket.getId());
        binding.detailDate.textDetailValue.setText(ticket.getTime());
        binding.detailPayment.textDetailValue.setText(ticket.getStatus());
        binding.detailSeats.textDetailValue.setText(ticket.getSeatNames().toString());
    }
}