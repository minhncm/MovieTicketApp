package com.example.ticketapp.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.ticketapp.domain.model.Res.UpdateProfileRequest;
import com.example.ticketapp.utils.Resource;
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
        // Load basic user info from Account
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                updateBasicUserInfo(user);
                
                // Load detailed profile from API
                if (user.getUid() != null) {
                    loadUserProfileDetails(user.getUid());
                }
            }
        });
    }

    private void loadUserProfileDetails(String uid) {
        profileViewModel.getUserProfileDetails(uid).observe(getViewLifecycleOwner(), this::handleUserProfileResponse);
    }

    private void handleUserProfileResponse(Resource<Account> resource) {
        if (resource != null) {
            switch (resource.getStatus()) {
                case LOADING:
                    showLoadingState();
                    break;
                case SUCCESS:
                    hideLoadingState();
                    if (resource.getData() != null) {
                        updateUserProfileInfo(resource.getData());
                    }
                    break;
                case ERROR:
                    hideLoadingState();
                    Toast.makeText(getContext(), "Lỗi: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
                    // Fallback to placeholder data
                    updatePlaceholderData();
                    break;
            }
        }
    }

    private void updateBasicUserInfo(Account user) {
        // Basic Info from Account
        binding.tvUserName.setText(user.getUsername());
        binding.tvUserEmail.setText(user.getEmail());
        
        // Load avatar
        Glide.with(this)
                .load(user.getPosterUrl())
                .placeholder(R.drawable.avt)
                .error(R.drawable.avt)
                .circleCrop()
                .into(binding.ivUserAvatar);
    }

    private void updateUserProfileInfo(Account account) {
        // Update with API data
        binding.tvUserEmail.setText(account.getEmail());
        binding.tvPhoneNumber.setText(account.getPhoneNumber() != null ? account.getPhoneNumber() : "Chưa cập nhật");
        binding.tvGender.setText(account.getGender() != null ? account.getGender() : "Chưa cập nhật");
        binding.tvAddress.setText(account.getAddress() != null ? account.getAddress() : "Chưa cập nhật");
        
        // Format member since date
        String memberSince = account.getMemberSince() != null ? 
            formatDate(account.getMemberSince()) : formatMemberSince();
        binding.tvMemberSince.setText(memberSince);
        
        // Statistics
        binding.tvTotalTickets.setText(account.getTotalTickets() + " vé");
        binding.tvRewardPoints.setText(String.format(Locale.getDefault(), "%,d điểm", account.getRewardPoints()));
    }

    private void updatePlaceholderData() {
        // Fallback placeholder data when API fails
        binding.tvPhoneNumber.setText("Chưa cập nhật");
        binding.tvGender.setText("Chưa cập nhật");
        binding.tvAddress.setText("Chưa cập nhật");
        binding.tvMemberSince.setText(formatMemberSince());
        binding.tvTotalTickets.setText("0 vé");
        binding.tvRewardPoints.setText("0 điểm");
    }

    private void showLoadingState() {
        // Show loading indicators for data fields
        binding.tvPhoneNumber.setText("Đang tải...");
        binding.tvGender.setText("Đang tải...");
        binding.tvAddress.setText("Đang tải...");
        binding.tvMemberSince.setText("Đang tải...");
        binding.tvTotalTickets.setText("Đang tải...");
        binding.tvRewardPoints.setText("Đang tải...");
    }

    private void hideLoadingState() {
        // Loading state will be replaced by actual data or placeholder
    }

    private String formatDate(String dateString) {
        // Format date from API if needed
        // For now, return as is since API format is not specified
        return dateString;
    }

    private String formatMemberSince() {
        // Placeholder - in real app, this would come from user data
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000))); // 1 year ago
    }

    private void setupClickListeners() {
        // Edit Display Name
        binding.btnEditName.setOnClickListener(v -> showEditNameDialog());

        // Change Password
        binding.layoutChangePassword.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PersonalDataFragment.this);
            navController.navigate(R.id.action_personalDataFragment_to_changePassword);
        });

        // Update Phone
        binding.layoutUpdatePhone.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PersonalDataFragment.this);
            navController.navigate(R.id.action_personalDataFragment_to_updatePhone);
        });

        // Update Address
        binding.layoutUpdateAddress.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PersonalDataFragment.this);
            navController.navigate(R.id.action_personalDataFragment_to_updateAddress);
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

    private void showEditNameDialog() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Không thể chỉnh sửa", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme);
        builder.setTitle("Chỉnh sửa tên hiển thị");

        // Create EditText
        final EditText input = new EditText(requireContext());
        input.setText(currentUser.getUsername());
        input.setTextColor(getResources().getColor(android.R.color.white, null));
        input.setHintTextColor(getResources().getColor(android.R.color.darker_gray, null));
        input.setPadding(48, 32, 48, 32);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            updateDisplayName(newName);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateDisplayName(String newName) {
        if (currentUser == null || currentUser.getUid() == null) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest(currentUser.getUid());
        request.setDisplayName(newName);
        request.setEmail(currentUser.getEmail());
        request.setPhoneNumber(currentUser.getPhoneNumber());
        request.setGender(currentUser.getGender());
        request.setAddress(currentUser.getAddress());

        profileViewModel.updateProfile(request).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case LOADING:
                        Toast.makeText(getContext(), "Đang cập nhật...", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        // Update UI
                        binding.tvUserName.setText(newName);
                        currentUser.setUsername(newName);
                        profileViewModel.setUserProfile(currentUser);
                        break;
                    case ERROR:
                        Toast.makeText(getContext(), "Lỗi: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("PersonalDataFragment", "Update display name error: " + resource.getMessage());
                        break;
                }
            }
        });
    }
}