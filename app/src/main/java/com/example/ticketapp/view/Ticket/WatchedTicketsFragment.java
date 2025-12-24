package com.example.ticketapp.view.Ticket;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ticketapp.R;
import com.example.ticketapp.adapter.TicketAdapter;
import com.example.ticketapp.databinding.FragmentUpcomingTicketsBinding;
import com.example.ticketapp.databinding.FragmentWatchedTicketsBinding;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.view.Movie.MovieReviewFragment;
import com.example.ticketapp.viewmodel.BookingViewModel;
import com.example.ticketapp.viewmodel.MovieViewModel;
import java.util.List;
public class WatchedTicketsFragment extends Fragment {
    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }
    private BookingViewModel bookingViewModel; // Giả sử ViewModel quản lý việc lấy vé
    private MovieViewModel movieViewModel;
    private TicketAdapter adapter;
    private FragmentWatchedTicketsBinding binding;
    private  OnTicketClickListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof  OnTicketClickListener) {
            listener = (OnTicketClickListener) getParentFragment();
        } else {
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWatchedTicketsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel và Adapter/RecyclerView
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);

        RecyclerView recyclerView = binding.recyclerWatchedTickets;
        adapter = new TicketAdapter(false);
        adapter.setOnTicketActionListener(new TicketAdapter.OnTicketActionListener() {
            @Override
            public void onCancelTicket(Ticket ticket) {

            }

            @Override
            public void onRateTicket(Ticket ticket) {
                String movieId = ticket.getMovieId();
                String bookingId = ticket.getId();
                
                if (movieId == null || movieId.isEmpty()) {
                    android.widget.Toast.makeText(getContext(), "Không tìm thấy thông tin phim", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Tạo Movie object tối thiểu với movieId và movieName
                Movie movie = new Movie(
                    movieId,
                    ticket.getMovieName(),
                    null, // director
                    0.0, // rating
                    null, // genres
                    null, // duration
                    null, // synopsis
                    null, // posterUrl
                    null, // status
                    null  // releaseDate
                );
                
                // Set selected movie trong MovieViewModel
                movieViewModel.setSelectMovie(movie);
                
                // Mở MovieReviewFragment với quyền đánh giá
                MovieReviewFragment reviewFragment = MovieReviewFragment.newInstance(true);
                android.os.Bundle args = reviewFragment.getArguments();
                if (args != null) {
                    args.putString("bookingId", bookingId);
                }
                
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, reviewFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onTicketClick(Ticket ticket) {
                listener.onTicketClick(ticket);

            }
        });// Truyền cờ là vé sắp tới (Upcoming = true)
        recyclerView.setAdapter(adapter);
        // Bắt đầu quan sát dữ liệu
        observeWatchedTickets();
    }

    private void observeWatchedTickets() {

            bookingViewModel.getWatchedTickets().observe(getViewLifecycleOwner(), resource -> {
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

}