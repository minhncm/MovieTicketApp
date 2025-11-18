package com.example.ticketapp.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ticketapp.MainActivity;
import com.example.ticketapp.databinding.FragmentLoginWithPasswordBinding;
import com.example.ticketapp.viewmodel.ProfileViewModel;

import java.util.Objects;


public class LoginWithPasswordFragment extends Fragment {

    private FragmentLoginWithPasswordBinding binding;
    private ProfileViewModel profileViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginWithPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
setUpViewModel();
        binding.btnSignIn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.edtEmail.getText()).toString();
            String password = Objects.requireNonNull(binding.edtPassword.getText()).toString();

            if (password.isEmpty() || email.isEmpty()) {
                binding.edtPassword.setError("Please enter your email and password");
                binding.edtPassword.requestFocus();
                return;
            }
            profileViewModel.login(email,
                    password).observe(getViewLifecycleOwner(),
                    result -> {
                        Log.d("LoginResult", "Login result: " + result.getMessage());
                        if (result.isSuccess()) {
                            profileViewModel.setUserProfile(result.getUser());
                            startActivity(new Intent(getActivity(), MainActivity.class));

                            getActivity().finish(); // Optional: finish the current activity
                        } else {
                            binding.edtPassword.setError("Email or Password is incorrect");
                            binding.edtPassword.requestFocus();
                        }
                    });

        });

    }

    private void setUpViewModel() {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    }
}