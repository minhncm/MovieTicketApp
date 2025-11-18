package com.example.ticketapp.view.Ticket;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ticketapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ticketapp.adapter.TicketViewPagerAdapter;
import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.viewmodel.BookingViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

public class MyTicket extends Fragment implements UpcomingTicketsFragment.OnTicketClickListener,
        WatchedTicketsFragment.OnTicketClickListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BookingViewModel bookingViewModel;
    private final List<String> tabTitles = Arrays.asList("Phim sắp xem", "Phim đã xem");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout đã có (activity_ticket_list.xml hoặc tương đương)
        return inflater.inflate(R.layout.fragment_my_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // 1. Khởi tạo và gán Adapter
        // QUAN TRỌNG: Truyền 'this' (Fragment hiện tại) vào Adapter
        TicketViewPagerAdapter adapter = new TicketViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 2. Liên kết TabLayout và ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    // Đặt tiêu đề cho tab
                    tab.setText(tabTitles.get(position));
                }
        ).attach();

        // Cài đặt tab mặc định là "Phim đã xem" (index 1), nếu cần
        viewPager.setCurrentItem(1, false); // false để không có animation khi khởi tạo
    }

    @Override
    public void onTicketClick(Ticket ticket) {
        bookingViewModel.setCurrentTicket(ticket);
        NavController navController = NavHostFragment.findNavController(MyTicket.this);
        navController.navigate(MyTicketDirections.actionMyTicketToETicket());
    }
}