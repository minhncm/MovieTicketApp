package com.example.ticketapp.view;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentChangePasswordBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangePasswordFragment extends Fragment {
    private FragmentChangePasswordBinding binding;
    private FirebaseAuth firebaseAuth;
    
    // Password validation flags
    private boolean hasMinLength = false;
    private boolean hasUppercase = false;
    private boolean hasNumber = false;
    private boolean hasSpecialChar = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        firebaseAuth = FirebaseAuth.getInstance();
        
        setupPasswordValidation();
        setupClickListeners();
    }

    private void setupPasswordValidation() {
        binding.etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validatePassword(String password) {
        // Check minimum length (8 characters)
        hasMinLength = password.length() >= 8;
        updateCheckIcon(binding.ivCheckLength, hasMinLength);

        // Check uppercase letter
        hasUppercase = password.matches(".*[A-Z].*");
        updateCheckIcon(binding.ivCheckUppercase, hasUppercase);

        // Check number
        hasNumber = password.matches(".*\\d.*");
        updateCheckIcon(binding.ivCheckNumber, hasNumber);

        // Check special character
        hasSpecialChar = password.matches(".*[@#$%^&+=!].*");
        updateCheckIcon(binding.ivCheckSpecial, hasSpecialChar);
    }

    private void updateCheckIcon(android.widget.ImageView imageView, boolean isValid) {
        if (isValid) {
            imageView.setColorFilter(Color.parseColor("#4CAF50")); // Green
        } else {
            imageView.setColorFilter(Color.parseColor("#636882")); // Gray
        }
    }

    private boolean isPasswordValid() {
        return hasMinLength && hasUppercase && hasNumber && hasSpecialChar;
    }

    private void setupClickListeners() {
        // Cancel button
        binding.btnCancel.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(ChangePasswordFragment.this);
            navController.navigateUp();
        });

        // Save password button
        binding.btnSavePassword.setOnClickListener(v -> {
            changePassword();
        });
    }

    private void changePassword() {
        String currentPassword = binding.etCurrentPassword.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmNewPassword.getText().toString().trim();

        // Validate inputs
        if (currentPassword.isEmpty()) {
            binding.tilCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        }

        if (newPassword.isEmpty()) {
            binding.tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (!isPasswordValid()) {
            binding.tilNewPassword.setError("Mật khẩu không đáp ứng yêu cầu");
            return;
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmNewPassword.setError("Vui lòng xác nhận mật khẩu mới");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.tilConfirmNewPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            binding.tilNewPassword.setError("Mật khẩu mới phải khác mật khẩu hiện tại");
            return;
        }

        // Clear errors
        binding.tilCurrentPassword.setError(null);
        binding.tilNewPassword.setError(null);
        binding.tilConfirmNewPassword.setError(null);

        // Disable buttons during processing
        binding.btnSavePassword.setEnabled(false);
        binding.btnCancel.setEnabled(false);

        // Change password in Firebase
        updatePasswordInFirebase(currentPassword, newPassword);
    }

    private void updatePasswordInFirebase(String currentPassword, String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        
        if (user == null || user.getEmail() == null) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            enableButtons();
            return;
        }

        // Re-authenticate user before changing password
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        
        user.reauthenticate(credential)
            .addOnSuccessListener(aVoid -> {
                // Re-authentication successful, now update password
                user.updatePassword(newPassword)
                    .addOnSuccessListener(aVoid1 -> {
                        Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        
                        // Navigate back
                        NavController navController = NavHostFragment.findNavController(ChangePasswordFragment.this);
                        navController.navigateUp();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        enableButtons();
                    });
            })
            .addOnFailureListener(e -> {
                binding.tilCurrentPassword.setError("Mật khẩu hiện tại không đúng");
                Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                enableButtons();
            });
    }

    private void enableButtons() {
        binding.btnSavePassword.setEnabled(true);
        binding.btnCancel.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
