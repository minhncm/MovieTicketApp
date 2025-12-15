package com.example.ticketapp.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentEditProfileBinding;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.viewmodel.ProfileViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private Account currentAccount;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        Glide.with(this)
                                .load(selectedImageUri)
                                .circleCrop()
                                .into(binding.ivProfileAvatar);
                    }
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
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
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                currentAccount = account;
                populateFields(account);
                
                // Load detailed profile from API
                if (account.getUid() != null) {
                    loadUserProfileDetails(account.getUid());
                }
            }
        });
    }

    private void loadUserProfileDetails(String uid) {
        profileViewModel.getUserProfileDetails(uid).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.getData() != null) {
                currentAccount = resource.getData();
                populateFields(resource.getData());
            }
        });
    }

    private void populateFields(Account account) {
        // Load avatar
        Glide.with(this)
                .load(account.getPosterUrl())
                .placeholder(R.drawable.avt)
                .error(R.drawable.avt)
                .circleCrop()
                .into(binding.ivProfileAvatar);

        // Populate form fields
        binding.etUsername.setText(account.getUsername());
        binding.etEmail.setText(account.getEmail());
        binding.etPhoneNumber.setText(account.getPhoneNumber());
        binding.etAddress.setText(account.getAddress());

        // Set gender
        String gender = account.getGender();
        if (gender != null) {
            if (gender.equalsIgnoreCase("Nam") || gender.equalsIgnoreCase("Male")) {
                binding.rbMale.setChecked(true);
            } else if (gender.equalsIgnoreCase("Nữ") || gender.equalsIgnoreCase("Female")) {
                binding.rbFemale.setChecked(true);
            } else {
                binding.rbOther.setChecked(true);
            }
        }
    }

    private void setupClickListeners() {
        // Change Avatar
        binding.btnChangeAvatar.setOnClickListener(v -> openImagePicker());

        // Cancel Button
        binding.btnCancel.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(EditProfileFragment.this);
            navController.navigateUp();
        });

        // Save Button
        binding.btnSave.setOnClickListener(v -> saveProfile());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveProfile() {
        // Validate inputs
        String username = binding.etUsername.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();

        if (username.isEmpty()) {
            binding.etUsername.setError("Vui lòng nhập tên người dùng");
            binding.etUsername.requestFocus();
            return;
        }

        // Get selected gender
        String gender = "Khác";
        if (binding.rbMale.isChecked()) {
            gender = "Nam";
        } else if (binding.rbFemale.isChecked()) {
            gender = "Nữ";
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSave.setEnabled(false);

        // Update account object
        currentAccount.setUsername(username);
        currentAccount.setPhoneNumber(phoneNumber);
        currentAccount.setAddress(address);
        currentAccount.setGender(gender);

        // TODO: Upload image if selectedImageUri is not null
        // For now, keep existing posterUrl

        // Call API to update profile
        profileViewModel.updateUserProfile(currentAccount).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case LOADING:
                        // Already showing loading
                        break;
                    case SUCCESS:
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnSave.setEnabled(true);
                        
                        Toast.makeText(getContext(), "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                        
                        // Navigate back
                        NavController navController = NavHostFragment.findNavController(EditProfileFragment.this);
                        navController.navigateUp();
                        break;
                    case ERROR:
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnSave.setEnabled(true);
                        
                        Toast.makeText(getContext(), "Lỗi: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
