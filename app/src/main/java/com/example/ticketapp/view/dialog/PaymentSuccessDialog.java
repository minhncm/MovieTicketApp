package com.example.ticketapp.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ticketapp.R; // Thay đổi package của bạn
import com.example.ticketapp.databinding.DialogPaymentSuccessBinding; // Import ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PaymentSuccessDialog extends BottomSheetDialogFragment {

    private DialogPaymentSuccessBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogPaymentSuccessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSeeTicket.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            int startDestinationId = navController.getGraph().getStartDestinationId();
            NavDirections action = PaymentSuccessDialogDirections.actionPaymentSuccessDialogToETicket();
            // 3. Tạo NavOptions để xóa TOÀN BỘ back stack
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(startDestinationId, true)
                    .build();
            navController.navigate(action, navOptions);
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}