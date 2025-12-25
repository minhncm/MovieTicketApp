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

import com.example.ticketapp.databinding.FragmentUpdatePhoneBinding;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.domain.model.Res.UpdateProfileRequest;
import com.example.ticketapp.viewmodel.ProfileViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UpdatePhoneFragment extends Fragment {
    private FragmentUpdatePhoneBinding binding;
    private ProfileViewModel profileViewModel;
    private Account currentUser;
    private String currentPhone = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdatePhoneBinding.inflate(inflater, container, false);
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
                // Hiển thị số điện thoại hiện tại (nếu có)
                currentPhone = user.getPhoneNumber() != null ? user.getPhoneNumber() : "+84 123 456 789";
                binding.tvCurrentPhone.setText(currentPhone);
            }
        });
    }

    private void setupClickListeners() {
        // Nút Hủy
        binding.btnCancel.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(UpdatePhoneFragment.this);
            navController.navigateUp();
        });

        // Nút Xác Nhận
        binding.btnConfirm.setOnClickListener(v -> {
            updatePhoneNumber();
        });
    }

    private void updatePhoneNumber() {
        String newPhone = binding.etNewPhone.getText().toString().trim();

        // Validate input
        if (newPhone.isEmpty()) {
            binding.tilNewPhone.setError("Vui lòng nhập số điện thoại mới");
            return;
        }

        // Validate định dạng số điện thoại Việt Nam
        if (!isValidVietnamesePhone(newPhone)) {
            binding.tilNewPhone.setError("Số điện thoại không hợp lệ");
            return;
        }

        // Chuẩn hóa số điện thoại (chuyển 0 thành +84)
        String normalizedPhone = normalizePhoneNumber(newPhone);

        // Kiểm tra số mới có khác số cũ không
        if (normalizedPhone.equals(currentPhone)) {
            binding.tilNewPhone.setError("Số điện thoại mới phải khác số hiện tại");
            return;
        }

        // Clear error
        binding.tilNewPhone.setError(null);

        // Disable buttons
        binding.btnConfirm.setEnabled(false);
        binding.btnCancel.setEnabled(false);

        // Cập nhật vào Firestore
        updatePhoneInFirestore(normalizedPhone);
    }

    private boolean isValidVietnamesePhone(String phone) {
        // Loại bỏ khoảng trắng và dấu gạch ngang
        phone = phone.replaceAll("[\\s-]", "");
        
        // Kiểm tra định dạng:
        // - Bắt đầu bằng +84 hoặc 84 hoặc 0
        // - Theo sau là 9-10 số
        return phone.matches("^(\\+84|84|0)[0-9]{9,10}$");
    }

    private String normalizePhoneNumber(String phone) {
        // Loại bỏ khoảng trắng và dấu gạch ngang
        phone = phone.replaceAll("[\\s-]", "");
        
        // Chuyển đổi sang định dạng +84
        if (phone.startsWith("0")) {
            phone = "+84" + phone.substring(1);
        } else if (phone.startsWith("84")) {
            phone = "+" + phone;
        } else if (!phone.startsWith("+84")) {
            phone = "+84" + phone;
        }
        
        return phone;
    }

    private void updatePhoneInFirestore(String newPhone) {
        if (currentUser == null || currentUser.getUid() == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            enableButtons();
            return;
        }

        // Tạo request để gọi API
        UpdateProfileRequest request = new UpdateProfileRequest(currentUser.getUid());
        request.setPhoneNumber(newPhone);
        request.setEmail(currentUser.getEmail());
        request.setDisplayName(currentUser.getUsername());
        request.setGender(currentUser.getGender());
        request.setAddress(currentUser.getAddress());

        // Gọi API update profile
        profileViewModel.updateProfile(request).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case LOADING:
                        // Đang xử lý
                        break;
                    case SUCCESS:
                        Toast.makeText(getContext(), 
                            "Cập nhật số điện thoại thành công!", 
                            Toast.LENGTH_SHORT).show();
                        
                        // Cập nhật currentUser và notify ViewModel
                        currentUser.setPhoneNumber(newPhone);
                        profileViewModel.setUserProfile(currentUser);
                        
                        // Quay lại màn hình trước
                        NavController navController = NavHostFragment.findNavController(UpdatePhoneFragment.this);
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
