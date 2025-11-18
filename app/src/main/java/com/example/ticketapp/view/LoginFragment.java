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

import com.example.ticketapp.AuthenticationActivity;
import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentLoginBinding;
import com.example.ticketapp.viewmodel.ProfileViewModel;


public class LoginFragment extends Fragment {
        private FragmentLoginBinding binding;
        private ProfileViewModel profileViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentLoginBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModel();

        binding.tvRegister.setOnClickListener(view1 ->{
            RegisterFragment registerFragment = new RegisterFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentAuthenticationView, registerFragment)
                    .addToBackStack(null)
                    .commit();
        }

    );
        binding.btnPasswordLogin.setOnClickListener(view1 -> {
            LoginWithPasswordFragment loginWithPasswordFragment = new LoginWithPasswordFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentAuthenticationView, loginWithPasswordFragment)
                    .addToBackStack(null)
                    .commit();
        });

    }

    private void setUpViewModel() {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

    }
}