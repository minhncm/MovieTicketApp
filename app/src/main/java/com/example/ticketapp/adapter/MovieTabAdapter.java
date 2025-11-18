package com.example.ticketapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ticketapp.view.Movie.MovieListFragment;

import java.util.Arrays;
import java.util.List;

public class MovieTabAdapter extends FragmentStateAdapter {

    private final List<String> tabTitles = Arrays.asList("NOW_SHOWING", "COMING_SOON");

    public MovieTabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String filterType = tabTitles.get(position);
            return MovieListFragment.newInstance(filterType); // Tạo Fragment mới
    }

    @Override
    public int getItemCount() {
        return tabTitles.size();
    }
}