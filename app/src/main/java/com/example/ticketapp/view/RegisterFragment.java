package com.example.ticketapp.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ticketapp.MainActivity;
import com.example.ticketapp.databinding.FragmentRegisterBinding;
import com.example.ticketapp.viewmodel.ProfileViewModel;


public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModel();
        setUpView();
    }

    private void setUpView() {
        binding.btnSignUp.setOnClickListener(v -> {
            String name = binding.edtName.getText().toString().trim();
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();
            String confirmPassword = binding.edtConfirmPassword.getText().toString().trim();
            if (!password.equals(confirmPassword)) {
                binding.edtConfirmPassword.setError("Password does not match");
                binding.edtConfirmPassword.requestFocus();
                return;
            }
            profileViewModel.register(name, email, password).observe(getViewLifecycleOwner(), result -> {
                if (result.isSuccess()) {
                    // Registration successful, navigate to MainActivity
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish(); // Optional: finish the current activity
                } else {
                    // Registration failed, show error message
                    binding.edtEmail.setError(result.getMessage());
                    binding.edtEmail.requestFocus();
                }
            });
        });
    }

    private void setUpViewModel() {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    }
}