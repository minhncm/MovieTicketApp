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

import com.example.ticketapp.databinding.FragmentUpdateAddressBinding;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.domain.model.Res.UpdateProfileRequest;
import com.example.ticketapp.viewmodel.ProfileViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UpdateAddressFragment extends Fragment {
    private FragmentUpdateAddressBinding binding;
    private ProfileViewModel profileViewModel;
    private Account currentUser;
    private String currentAddress = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateAddressBinding.inflate(inflater, container, false);
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
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                // Hiển thị địa chỉ hiện tại (nếu có)
                currentAddress = user.getAddress() != null ? user.getAddress() : "123 Đường ABC, Quận 1, TP.HCM";
                binding.tvCurrentAddress.setText(currentAddress);
                
                // Parse địa chỉ hiện tại để điền vào form (nếu có)
                parseAndFillAddress(currentAddress);
            }
        });
    }

    private void parseAndFillAddress(String address) {
        // Parse địa chỉ theo format: "Số nhà, Phường, Quận, Thành phố"
        if (address != null && !address.isEmpty()) {
            String[] parts = address.split(",");
            if (parts.length >= 1) {
                binding.etStreet.setText(parts[0].trim());
            }
            if (parts.length >= 2) {
                binding.etWard.setText(parts[1].trim());
            }
            if (parts.length >= 3) {
                binding.etDistrict.setText(parts[2].trim());
            }
            if (parts.length >= 4) {
                binding.etCity.setText(parts[3].trim());
            }
        }
    }

    private void setupClickListeners() {
        // Nút Hủy
        binding.btnCancel.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(UpdateAddressFragment.this);
            navController.navigateUp();
        });

        // Nút Xác Nhận
        binding.btnConfirm.setOnClickListener(v -> {
            updateAddress();
        });
    }

    private void updateAddress() {
        String street = binding.etStreet.getText().toString().trim();
        String ward = binding.etWard.getText().toString().trim();
        String district = binding.etDistrict.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();

        // Validate input
        if (street.isEmpty()) {
            binding.tilStreet.setError("Vui lòng nhập số nhà/tên đường");
            return;
        }

        if (ward.isEmpty()) {
            binding.tilWard.setError("Vui lòng nhập phường/xã");
            return;
        }

        if (district.isEmpty()) {
            binding.tilDistrict.setError("Vui lòng nhập quận/huyện");
            return;
        }

        if (city.isEmpty()) {
            binding.tilCity.setError("Vui lòng nhập tỉnh/thành phố");
            return;
        }

        // Tạo địa chỉ đầy đủ
        String fullAddress = street + ", " + ward + ", " + district + ", " + city;

        // Kiểm tra địa chỉ mới có khác địa chỉ cũ không
        if (fullAddress.equals(currentAddress)) {
            Toast.makeText(getContext(), "Địa chỉ mới phải khác địa chỉ hiện tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear errors
        binding.tilStreet.setError(null);
        binding.tilWard.setError(null);
        binding.tilDistrict.setError(null);
        binding.tilCity.setError(null);

        // Disable buttons
        binding.btnConfirm.setEnabled(false);
        binding.btnCancel.setEnabled(false);

        // Cập nhật vào Firestore
        updateAddressInFirestore(fullAddress);
    }

    private void updateAddressInFirestore(String newAddress) {
        if (currentUser == null || currentUser.getUid() == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            enableButtons();
            return;
        }

        // Tạo request để gọi API
        UpdateProfileRequest request = new UpdateProfileRequest(currentUser.getUid());
        request.setAddress(newAddress);
        request.setEmail(currentUser.getEmail());
        request.setDisplayName(currentUser.getUsername());
        request.setPhoneNumber(currentUser.getPhoneNumber());
        request.setGender(currentUser.getGender());

        // Gọi API update profile
        profileViewModel.updateProfile(request).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case LOADING:
                        // Đang xử lý
                        break;
                    case SUCCESS:
                        Toast.makeText(getContext(), 
                            "Cập nhật địa chỉ thành công!", 
                            Toast.LENGTH_SHORT).show();
                        
                        // Cập nhật currentUser và notify ViewModel
                        currentUser.setAddress(newAddress);
                        profileViewModel.setUserProfile(currentUser);
                        
                        // Quay lại màn hình trước
                        NavController navController = NavHostFragment.findNavController(UpdateAddressFragment.this);
                        navController.navigateUp();
                        break;
                    case ERROR:
                        Toast.makeText(getContext(), 
                            "Lỗi: " + resource.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                        enableButtons();
                        break;
                }
            }
        });
    }

    private void enableButtons() {
        binding.btnConfirm.setEnabled(true);
        binding.btnCancel.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
