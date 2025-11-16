package com.example.ticketapp.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.ticketapp.R;
import com.example.ticketapp.adapter.CinemaAdapter;
import com.example.ticketapp.adapter.MovieTabAdapter; // Adapter mới
import com.example.ticketapp.databinding.FragmentHomeBinding;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.view.Movie.MovieListFragment;
import com.example.ticketapp.viewmodel.CinemaViewModel;
import com.example.ticketapp.viewmodel.MovieViewModel;
import com.example.ticketapp.viewmodel.ProfileViewModel;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.auth.User;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.Arrays;
import java.util.List;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements MovieListFragment.OnMovieSelectListener {

    private FragmentHomeBinding binding;
    private MovieViewModel movieViewModel;
    private ProfileViewModel profileViewModel;
    private TabLayoutMediator tabLayoutMediator;
    private MovieTabAdapter movieTabAdapter; // Adapter mới
    private CinemaViewModel cinemaViewModel;
    private RecyclerView cinemaRecyclerView;
    private CinemaAdapter cinemaAdapter;
    private  NavController navController;

    private final List<String> tabTitles = Arrays.asList("Đang chiếu", "Sắp chiếu");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        setupViewModel();
        setupSwipeRefresh();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideToolbar();
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(),user -> {
            if(user != null){
                initView(user);
            }
        });
        navController = NavHostFragment.findNavController(HomeFragment.this);
        binding.ivAvatar.setOnClickListener(
                view1 -> {
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment());
                }
        );
        cinemaAdapter = new CinemaAdapter();
        cinemaRecyclerView = binding.rvCinemas;
        cinemaRecyclerView.setAdapter(cinemaAdapter);
        setupMovieTabsViewPager(); // Hợp nhất logic setup vào đây
    }

    private void initView( Account user) {
        binding.tvUsername.setText(user.getUsername());
        Glide.with(binding.getRoot().getContext())
                .load(user.getPosterUrl())
                .error(R.drawable.ic_launcher_background)
                .into(binding.ivAvatar);
    }


    private void setupMovieTabsViewPager() {
        // 1. Khởi tạo Adapter (FragmentStateAdapter)
        movieTabAdapter = new MovieTabAdapter(this);
        // 2. Gán Adapter cho ViewPager2
        binding.vpMovieSlider.setAdapter(movieTabAdapter);
        binding.vpMovieSlider.setUserInputEnabled(false);
        binding.vpMovieSlider.setPageTransformer(null);
        // 4. Liên kết TabLayout với ViewPager2
        tabLayoutMediator = new TabLayoutMediator(
                binding.tabMovieFilter,
                binding.vpMovieSlider,
                (tab, position) -> {
                    // Gán tiêu đề tab
                    tab.setText(tabTitles.get(position));
                }
        );
        tabLayoutMediator.attach();

        // Đặt tab mặc định
        binding.vpMovieSlider.setCurrentItem(0, false);
    }

    // ... (refreshData, setupSwipeRefresh giữ nguyên) ...

    private void setupViewModel() {
        // Lấy ViewModel (cần thiết cho các Fragment con để lấy dữ liệu)
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        cinemaViewModel = new ViewModelProvider(requireActivity()).get(CinemaViewModel.class);

        // Observer cho danh sách phim tổng thể
        movieViewModel.movies.observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    binding.swipeRefreshLayout.setRefreshing(true);
                    break;
                case SUCCESS:
                    binding.swipeRefreshLayout.setRefreshing(false);
                    break;
                case ERROR:
                    binding.swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        });
        cinemaViewModel.getAllCinema(5).observe(getViewLifecycleOwner(),resource->{
            switch (resource.getStatus()) {
                case LOADING:
                    binding.swipeRefreshLayout.setRefreshing(true);
                    break;
                case SUCCESS:
                    binding.swipeRefreshLayout.setRefreshing(false);
                    cinemaAdapter.updateListCinema(resource.getData());

                    break;
                case ERROR:
                    binding.swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        });

        // Tải dữ liệu lần đầu tiên (nếu chưa có)
        if (movieViewModel.movies.getValue() == null) {
            movieViewModel.getMovies();
        }
    }

    private void hideToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            // Lấy SupportActionBar và ẩn nó
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().hide();
            }
        }
    }

    private void showToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showToolbar();
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }
        binding = null;
    }

    private void refreshData() {
        movieViewModel.getMovies();

    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    @Override
    public void onMovieSelect(Movie movie) {
        if (movie != null) {
            movieViewModel.setSelectMovie(movie);

            navController.navigate(HomeFragmentDirections.actionNavHomeToDetailsFragment());
        }
    }
}




