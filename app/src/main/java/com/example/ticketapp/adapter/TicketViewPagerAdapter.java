package com.example.ticketapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.view.Ticket.UpcomingTicketsFragment;
import com.example.ticketapp.view.Ticket.WatchedTicketsFragment;
import com.example.ticketapp.viewmodel.BookingViewModel;

public class TicketViewPagerAdapter extends FragmentStateAdapter {

    public TicketViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new UpcomingTicketsFragment();
        } else {
            return new WatchedTicketsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}