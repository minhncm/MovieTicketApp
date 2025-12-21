package com.example.ticketapp.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.ticketapp.adapter.ExploreMovieAdapter;
import com.example.ticketapp.databinding.FragmentSearchResultBinding;
import com.example.ticketapp.viewmodel.MovieViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchResultFragment extends Fragment {
    
    private FragmentSearchResultBinding binding;
    private MovieViewModel movieViewModel;
    private ExploreMovieAdapter movieAdapter;
    private NavController navController;
    private TextWatcher searchTextWatcher;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        navController = NavHostFragment.findNavController(this);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        
        setupRecyclerView();
        setupSearchInput();
        observeSearchResults();
    }
    
    private void setupRecyclerView() {
        movieAdapter = new ExploreMovieAdapter(movie -> {
            movieViewModel.setSelectMovie(movie);
            navController.navigate(SearchResultFragmentDirections.actionSearchResultToDetailsFragment());
        });
        
        binding.rvSearchResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvSearchResults.setAdapter(movieAdapter);
    }
    
    private void setupSearchInput() {
        // Tạo TextWatcher
        searchTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (query.trim().isEmpty()) {
                    movieViewModel.clearSearch();
                } else {
                    movieViewModel.searchMovies(query);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        
        // Observe query từ ViewModel và hiển thị trong EditText
        movieViewModel.searchQuery.observe(getViewLifecycleOwner(), query -> {
            if (query != null && !query.isEmpty()) {
                // Remove listener tạm thời để tránh trigger search lại
                binding.etSearchInput.removeTextChangedListener(searchTextWatcher);
                binding.etSearchInput.setText(query);
                // Đặt cursor ở cuối text
                binding.etSearchInput.setSelection(query.length());
                // Add listener lại
                binding.etSearchInput.addTextChangedListener(searchTextWatcher);
            }
        });
        
        binding.etSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.etSearchInput.getText().toString();
                movieViewModel.searchMovies(query);
                // Ẩn bàn phím
                android.view.inputmethod.InputMethodManager imm = 
                    (android.view.inputmethod.InputMethodManager) requireActivity()
                        .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        
        // Add TextWatcher
        binding.etSearchInput.addTextChangedListener(searchTextWatcher);
        
        binding.etSearchInput.requestFocus();
    }
    
    private void observeSearchResults() {
        movieViewModel.searchResults.observe(getViewLifecycleOwner(), movies -> {
            if (movies == null || movies.isEmpty()) {
                binding.tvNoResults.setVisibility(View.VISIBLE);
                binding.rvSearchResults.setVisibility(View.GONE);
            } else {
                binding.tvNoResults.setVisibility(View.GONE);
                binding.rvSearchResults.setVisibility(View.VISIBLE);
                movieAdapter.updateMovies(movies);
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        movieViewModel.clearSearch();
        binding = null;
    }
}
