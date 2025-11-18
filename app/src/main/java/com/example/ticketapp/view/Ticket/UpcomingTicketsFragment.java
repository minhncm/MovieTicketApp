package com.example.ticketapp.view.Ticket;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketapp.adapter.TicketAdapter;
import com.example.ticketapp.databinding.FragmentUpcomingTicketsBinding;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.viewmodel.BookingViewModel;
import com.example.ticketapp.viewmodel.ProfileViewModel;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UpcomingTicketsFragment extends Fragment {
    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    private BookingViewModel bookingViewModel; // Giả sử ViewModel quản lý việc lấy vé
    private TicketAdapter adapter; // Adapter cho RecyclerView
    private FragmentUpcomingTicketsBinding binding;
    private  OnTicketClickListener listener;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUpcomingTicketsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnTicketClickListener) {
            listener = (OnTicketClickListener) getParentFragment();
        } else {
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel và Adapter/RecyclerView
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);

        RecyclerView recyclerView = binding.recyclerUpcomingTickets;
        adapter = new TicketAdapter(true); // Truyền cờ là vé sắp tới (Upcoming = true)'
        adapter.setOnTicketActionListener(new TicketAdapter.OnTicketActionListener() {
            @Override
            public void onCancelTicket(Ticket ticket) {
            }

            @Override
            public void onRateTicket(Ticket ticket) {
            }

            @Override
            public void onTicketClick(Ticket ticket) {
                handleTicketClick(ticket);
            }


        });
        recyclerView.setAdapter(adapter);

        // Bắt đầu quan sát dữ liệu
        observeUpcomingTickets();
    }

    private void observeUpcomingTickets() {

        bookingViewModel.getUpcomingTickets().observe(getViewLifecycleOwner(), resource -> {
              switch (resource.getStatus()) {
                case LOADING:
                    break;
                case SUCCESS:
                    List<Ticket> tickets = resource.getData();
                    adapter.submitList(tickets);
                    break;
                case ERROR:
                    break;
            }
        });

    }
    private void handleTicketClick(Ticket selectedTicket) {
        if (listener != null) {
            listener.onTicketClick(selectedTicket);
        }
    }

}