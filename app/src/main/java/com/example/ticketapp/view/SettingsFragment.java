package com.example.ticketapp.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.ticketapp.AuthenticationActivity;
import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentSettingsBinding;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.viewmodel.ProfileViewModel;


public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private ProfileViewModel profileViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileViewModel = new  ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), this::initView);
        binding.logout.setOnClickListener(v -> {
            profileViewModel.logout();
            startActivity(new Intent(getActivity(), AuthenticationActivity.class));
            getActivity().finish();

        });
        binding.myTicket.setOnClickListener(v->{
            NavController navController = NavHostFragment.findNavController(SettingsFragment.this);
            navController.navigate(SettingsFragmentDirections.actionNavSettingsToMyTicket());
        });
    }
    private void initView(Account user){
        binding.tvName.setText(user.getUsername());
        Glide.with(binding.getRoot().getContext())
                .load(user.getPosterUrl())
                .error(R.drawable.ic_launcher_background)
                .into(binding.ivAvatar);
    }
}