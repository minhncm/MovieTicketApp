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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ticketapp.R;
import com.example.ticketapp.adapter.SavedPlanAdapter;
import com.example.ticketapp.domain.model.SavedPlanEntity;
import com.example.ticketapp.databinding.FragmentSavedPlanBinding;
import com.example.ticketapp.utils.DialogHelper;
import com.example.ticketapp.viewmodel.SavedPlanViewModel;

public class SavedPlanFragment extends Fragment implements SavedPlanAdapter.OnPlanActionListener {

    private FragmentSavedPlanBinding binding;
    private SavedPlanViewModel viewModel;
    private SavedPlanAdapter adapter;

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
                binding.tvPlanCount.setText(plans.size() + " plans");
            } else {
                adapter.submitList(null);
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.recyclerViewPlans.setVisibility(View.GONE);
                binding.tvPlanCount.setText("0 plans");
            }
        });
    }

    @Override
    public void onCheckout(SavedPlanEntity plan) {
        // Validate plan has required data
        if (plan.getCinemaName() == null || plan.getDate() == null ||
                plan.getTime() == null || plan.getSelectedSeats() == null) {
            Toast.makeText(requireContext(), R.string.txt_complete_plan_first, Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to payment or booking confirmation
        Toast.makeText(requireContext(), R.string.txt_proceeding_checkout, Toast.LENGTH_SHORT).show();
        // TODO: Navigate to payment screen with plan data
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
    public void onItemClick(SavedPlanEntity plan) {
        // Navigate to edit plan or movie detail
        Toast.makeText(requireContext(), plan.getMovieTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
