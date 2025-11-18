package com.example.ticketapp.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.ticketapp.AuthenticationActivity;
import com.example.ticketapp.MainActivity;
import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentSettingsBinding;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.utils.LocaleHelper;
import com.example.ticketapp.viewmodel.ProfileViewModel;
import com.google.android.material.button.MaterialButton;


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
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), this::initView);
        
        binding.logout.setOnClickListener(v -> {
            profileViewModel.logout();
            startActivity(new Intent(getActivity(), AuthenticationActivity.class));
            getActivity().finish();
        });
        
        binding.myTicket.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(SettingsFragment.this);
            navController.navigate(SettingsFragmentDirections.actionNavSettingsToMyTicket());
        });
        
        binding.languageSettings.setOnClickListener(v -> showLanguageDialog());
    }
    
    private void initView(Account user) {
        binding.tvName.setText(user.getUsername());
        Glide.with(binding.getRoot().getContext())
                .load(user.getPosterUrl())
                .error(R.drawable.ic_launcher_background)
                .into(binding.ivAvatar);
    }
    
    private void showLanguageDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_language_selector, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        
        RadioButton radioEnglish = dialogView.findViewById(R.id.radioEnglish);
        RadioButton radioVietnamese = dialogView.findViewById(R.id.radioVietnamese);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        
        // Set current language
        String currentLanguage = LocaleHelper.getCurrentLanguage(requireContext());
        if ("vi".equals(currentLanguage)) {
            radioVietnamese.setChecked(true);
        } else {
            radioEnglish.setChecked(true);
        }
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnConfirm.setOnClickListener(v -> {
            String selectedLanguage = radioVietnamese.isChecked() ? "vi" : "en";
            
            if (!selectedLanguage.equals(currentLanguage)) {
                LocaleHelper.setLocale(requireContext(), selectedLanguage);
                dialog.dismiss();
                showRestartDialog();
            } else {
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
    
    private void showRestartDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.txt_language)
                .setMessage(R.string.txt_language_changed)
                .setPositiveButton(R.string.txt_restart_app, (dialog, which) -> {
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton(R.string.txt_restart_later, (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}