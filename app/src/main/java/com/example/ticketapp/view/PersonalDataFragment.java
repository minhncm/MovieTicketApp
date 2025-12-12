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

import com.bumptech.glide.Glide;
import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentPersonalDataBinding;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.viewmodel.ProfileViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PersonalDataFragment extends Fragment {
    private FragmentPersonalDataBinding binding;
    private ProfileViewModel profileViewModel;
    private Account currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalDataBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        
        setupObservers();
        setupClickListeners();
    }

    private void setupObservers() {
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), this::updateUserInfo);
    }

    private void updateUserInfo(Account user) {
        if (user != null) {
            currentUser = user;
            
            // Basic Info
            binding.tvUserName.setText(user.getUsername());
            binding.tvUserEmail.setText(user.getEmail());
            binding.tvUserId.setText(user.getUid());
            
            // Additional Info (placeholder data)
            binding.tvPhoneNumber.setText("+84 123 456 789");
            binding.tvDateOfBirth.setText("15/03/1995");
            binding.tvGender.setText("Nam");
            binding.tvAddress.setText("123 Đường ABC, Quận 1, TP.HCM");
            binding.tvMemberSince.setText(formatMemberSince());
            binding.tvTotalTickets.setText("25 vé");
            binding.tvRewardPoints.setText("1,250 điểm");
            
            // Load avatar
            Glide.with(this)
                    .load(user.getPosterUrl())
                    .placeholder(R.drawable.avt)
                    .error(R.drawable.avt)
                    .circleCrop()
                    .into(binding.ivUserAvatar);
        }
    }

    private String formatMemberSince() {
        // Placeholder - in real app, this would come from user data
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000))); // 1 year ago
    }

    private void setupClickListeners() {
        // Edit Profile Button
        binding.btnEditProfile.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PersonalDataFragment.this);
            navController.navigate(R.id.action_personalDataFragment_to_editProfile);
        });

        // Change Password
        binding.layoutChangePassword.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PersonalDataFragment.this);
            navController.navigate(R.id.action_personalDataFragment_to_changePassword);
        });

        // Update Phone
        binding.layoutUpdatePhone.setOnClickListener(v -> {
            // TODO: Navigate to update phone screen
            Toast.makeText(getContext(), "Chức năng cập nhật số điện thoại đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Update Address
        binding.layoutUpdateAddress.setOnClickListener(v -> {
            // TODO: Navigate to update address screen
            Toast.makeText(getContext(), "Chức năng cập nhật địa chỉ đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // View Ticket History
        binding.layoutTicketHistory.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PersonalDataFragment.this);
            navController.navigate(R.id.myTicket);
        });

        // View Reward Points
        binding.layoutRewardPoints.setOnClickListener(v -> {
            // TODO: Navigate to reward points screen
            Toast.makeText(getContext(), "Chức năng xem điểm thưởng đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}