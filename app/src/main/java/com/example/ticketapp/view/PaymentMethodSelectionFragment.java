package com.example.ticketapp.view;

import android.app.ProgressDialog;
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

import com.example.ticketapp.R;
import com.example.ticketapp.databinding.FragmentPaymentMethodSelectionBinding;
import com.example.ticketapp.domain.model.Res.BookingData;
import com.example.ticketapp.viewmodel.BookingViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentMethodSelectionFragment extends Fragment {
    
    private FragmentPaymentMethodSelectionBinding binding;
    private BookingViewModel bookingViewModel;
    private NavController navController;
    private ProgressDialog progressDialog;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaymentMethodSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        navController = NavHostFragment.findNavController(this);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        
        setupProgressDialog();
        setupClickListeners();
    }
    
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage(getString(R.string.txt_processing_payment));
        progressDialog.setCancelable(false);
    }
    
    private void setupClickListeners() {
        // MoMo Payment
        binding.cardMomo.setOnClickListener(v -> handleMoMoPayment());
        
        // Bank Card Payment
        binding.cardBankCard.setOnClickListener(v -> handleBankCardPayment());
        
        // ZaloPay Payment
        binding.cardZaloPay.setOnClickListener(v -> handleZaloPayPayment());
    }
    
    private void handleMoMoPayment() {
        BookingData bookingData = bookingViewModel.bookingDataMutableLiveData.getValue();
        
        if (bookingData == null) {
            Toast.makeText(requireContext(), R.string.msg_booking_missing, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Navigate đến MoMo QR Payment Fragment
        navController.navigate(R.id.action_paymentMethodSelectionFragment_to_momoQrPaymentFragment);
    }
    
    private void handleBankCardPayment() {
        // Navigate đến PaymentMethod fragment (thẻ ngân hàng)
        navController.navigate(R.id.action_paymentMethodSelectionFragment_to_paymentMethod);
    }
    
    private void handleZaloPayPayment() {
        // ZaloPay - Fix cứng (mock)
        Toast.makeText(requireContext(), R.string.txt_zalopay_coming_soon, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        binding = null;
    }
}
