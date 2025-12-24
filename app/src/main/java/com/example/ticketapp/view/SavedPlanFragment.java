package com.example.ticketapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ticketapp.R;
import com.example.ticketapp.adapter.SavedPlanAdapter;
import com.example.ticketapp.domain.model.Movie;
import com.example.ticketapp.domain.model.SavedPlanEntity;
import com.example.ticketapp.databinding.FragmentSavedPlanBinding;
import com.example.ticketapp.utils.DialogHelper;
import com.example.ticketapp.viewmodel.MovieViewModel;
import com.example.ticketapp.viewmodel.SavedPlanViewModel;

import java.util.ArrayList;
import java.util.List;

public class SavedPlanFragment extends Fragment implements SavedPlanAdapter.OnPlanActionListener {

    private FragmentSavedPlanBinding binding;
    private SavedPlanViewModel viewModel;
    private SavedPlanAdapter adapter;
    private MovieViewModel movieViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SavedPlanViewModel.class);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);

        setupRecyclerView();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new SavedPlanAdapter(this);
        binding.recyclerViewPlans.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewPlans.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getAllPlans().observe(getViewLifecycleOwner(), plans -> {
            if (plans != null && !plans.isEmpty()) {
                adapter.submitList(plans);
                binding.emptyState.setVisibility(View.GONE);
                binding.recyclerViewPlans.setVisibility(View.VISIBLE);
                binding.tvPlanCount.setText(plans.size() + " " + getString(R.string.txt_favorite_movies).toLowerCase());
            } else {
                adapter.submitList(null);
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.recyclerViewPlans.setVisibility(View.GONE);
                binding.tvPlanCount.setText("0 " + getString(R.string.txt_favorite_movies).toLowerCase());
            }
        });
    }

    @Override
    public void onBookNow(SavedPlanEntity plan) {
        // Tạo Movie object từ SavedPlanEntity để set vào MovieViewModel
        Movie movie = new Movie();
        movie.setId(plan.getMovieId());
        movie.setTitle(plan.getMovieTitle());
        movie.setPosterUrl(plan.getMoviePoster());
        movie.setRating(plan.getRating());
        movie.setDuration(plan.getDuration());
        
        // Parse genre string thành list
        if (plan.getGenre() != null && !plan.getGenre().isEmpty()) {
            List<String> genres = new ArrayList<>();
            genres.add(plan.getGenre());
            movie.setGenres(genres);
        }
        
        // Set selected movie
        movieViewModel.setSelectMovie(movie);
        
        // Navigate to select seat
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_nav_bookmark_to_selectSeatFragment);
    }

    @Override
    public void onDelete(SavedPlanEntity plan) {
        DialogHelper.showConfirmDialog(
                requireContext(),
                getString(R.string.txt_delete_plan),
                getString(R.string.txt_delete_plan_confirm),
                getString(R.string.txt_delete),
                getString(R.string.txt_cancel),
                () -> {
                    viewModel.delete(plan);
                    Toast.makeText(requireContext(), R.string.txt_plan_deleted, Toast.LENGTH_SHORT).show();
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
