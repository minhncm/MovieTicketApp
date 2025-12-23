package com.example.ticketapp.view.Ticket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ticketapp.R;
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
        // Set film title
        binding.textFilmTitle.setText(ticket.getMovieName());
        
        // Set labels
        binding.detailDate.labelDetail.setText(R.string.txt_date);
        binding.detailLocation.labelDetail.setText(R.string.txt_cinema_label);
        binding.detailPayment.labelDetail.setText(R.string.txt_total);
        binding.detailSeats.labelDetail.setText(R.string.txt_seats);
        binding.detailTime.labelDetail.setText(R.string.txt_time);
        binding.detailOrder.labelDetail.setText(R.string.txt_ticket_id);
        
        // Set values
        binding.detailDate.textDetailValue.setText(ticket.getTime() != null ? ticket.getTime().split(" ")[0] : "");
        binding.detailLocation.textDetailValue.setText(ticket.getCinemaName());
        binding.detailPayment.textDetailValue.setText(String.format("%,.0f đ", ticket.getTotalPrice()));
        binding.detailSeats.textDetailValue.setText(String.join(", ", ticket.getSeatNames()));
        binding.detailTime.textDetailValue.setText(ticket.getTime() != null && ticket.getTime().contains(" ") 
                ? ticket.getTime().split(" ")[1] : ticket.getTime());
        binding.detailOrder.textDetailValue.setText(ticket.getId());
    }
}