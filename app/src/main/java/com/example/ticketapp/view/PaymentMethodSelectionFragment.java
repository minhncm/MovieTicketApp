package com.example.ticketapp.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.ticketapp.utils.MoMoPaymentHelper;
import com.example.ticketapp.viewmodel.BookingViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentMethodSelectionFragment extends Fragment {
    
    private static final String TAG = "PaymentMethodSelection";
    private FragmentPaymentMethodSelectionBinding binding;
    private BookingViewModel bookingViewModel;
    private NavController navController;
    private ProgressDialog progressDialog;
    private BroadcastReceiver momoPaymentReceiver;
    
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
        registerMoMoPaymentReceiver();
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
    
    private void registerMoMoPaymentReceiver() {
        momoPaymentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String resultCode = intent.getStringExtra("resultCode");
                String orderId = intent.getStringExtra("orderId");
                String message = intent.getStringExtra("message");
                
                Log.d(TAG, "MoMo callback received - ResultCode: " + resultCode);
                
                if ("0".equals(resultCode)) {
                    // Thanh toán thành công
                    Toast.makeText(requireContext(), R.string.txt_payment_success, Toast.LENGTH_SHORT).show();
                    processBooking();
                } else {
                    // Thanh toán thất bại
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), 
                            getString(R.string.txt_payment_failed) + ": " + message, 
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        
        IntentFilter filter = new IntentFilter("com.example.ticketapp.MOMO_PAYMENT_RESULT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(momoPaymentReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }
    }
    
    private void handleMoMoPayment() {
        BookingData bookingData = bookingViewModel.bookingDataMutableLiveData.getValue();
        
        if (bookingData == null) {
            Toast.makeText(requireContext(), R.string.msg_booking_missing, Toast.LENGTH_SHORT).show();
            return;
        }
        
        progressDialog.show();
        
        // Tính tổng tiền (giả sử mỗi ghế 100,000 VND)
        long amount = bookingData.getSelectedSeats().size() * 100000L;
        String orderInfo = "Thanh toán vé xem phim - " + bookingData.getSelectedSeats().size() + " ghế";
        
        MoMoPaymentHelper.createPayment(amount, orderInfo, new MoMoPaymentHelper.MoMoPaymentCallback() {
            @Override
            public void onSuccess(String payUrl, String orderId) {
                requireActivity().runOnUiThread(() -> {
                    progressDialog.setMessage("Đang chuyển đến MoMo...");
                    
                    // Mở trình duyệt để thanh toán
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
                    startActivity(browserIntent);
                    
                    // Giữ progress dialog để chờ callback
                    progressDialog.setMessage("Đang chờ thanh toán...");
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), 
                            getString(R.string.txt_momo_payment_error) + ": " + errorMessage, 
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void handleBankCardPayment() {
        // Navigate đến PaymentMethod fragment (thẻ ngân hàng)
        navController.navigate(R.id.action_paymentMethodSelectionFragment_to_paymentMethod);
    }
    
    private void handleZaloPayPayment() {
        // ZaloPay - Fix cứng (mock)
        Toast.makeText(requireContext(), R.string.txt_zalopay_coming_soon, Toast.LENGTH_SHORT).show();
    }
    
    private void processBooking() {
        BookingData bookingData = bookingViewModel.bookingDataMutableLiveData.getValue();
        if (bookingData == null) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            return;
        }
        
        if (progressDialog != null) {
            progressDialog.setMessage("Đang đặt vé...");
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
        
        bookingViewModel.bookingTicket(bookingData).observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            
            Log.d(TAG, "Booking status: " + resource.getStatus());
            
            switch (resource.getStatus()) {
                case LOADING:
                    // Đang loading
                    Log.d(TAG, "Booking loading...");
                    break;
                    
                case SUCCESS:
                    Log.d(TAG, "Booking success!");
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    
                    // Tạo ticket và navigate
                    if (resource.getData() != null) {
                        // Lấy totalPrice từ backend, nếu = 0 thì tính theo số ghế
                        double totalPrice = resource.getData().getTotalPrice();
                        if (totalPrice == 0 && resource.getData().getSeatNames() != null) {
                            // Tính giá: mỗi ghế 100,000 VND
                            totalPrice = resource.getData().getSeatNames().size() * 100000.0;
                            Log.d(TAG, "Backend returned 0, calculated price: " + totalPrice);
                        }
                        
                        com.example.ticketapp.domain.model.Ticket ticket = 
                                new com.example.ticketapp.domain.model.Ticket(
                                        resource.getData().getMovieName(),
                                        resource.getData().getCinemaName(),
                                        resource.getData().getSeatNames(),
                                        resource.getData().getShowStartTime(),
                                        resource.getData().getStatus(),
                                        resource.getData().getId(),
                                        totalPrice
                                );
                        bookingViewModel.setCurrentTicket(ticket);
                        
                        // Navigate sau khi dismiss dialog
                        new android.os.Handler().postDelayed(() -> {
                            try {
                                navController.navigate(R.id.action_paymentMethodSelectionFragment_to_paymentSuccessDialog);
                            } catch (Exception e) {
                                Log.e(TAG, "Navigation error", e);
                            }
                        }, 300);
                    }
                    break;
                    
                case ERROR:
                    Log.e(TAG, "Booking error: " + resource.getMessage());
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(requireContext(), 
                            "Lỗi đặt vé: " + resource.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Không dismiss dialog khi pause vì đang chờ callback từ MoMo
        Log.d(TAG, "Fragment paused");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed");
        
        // Kiểm tra payment result từ SharedPreferences
        checkPaymentResult();
    }
    
    private void checkPaymentResult() {
        android.content.SharedPreferences prefs = requireActivity()
                .getSharedPreferences("momo_payment", Context.MODE_PRIVATE);
        String resultCode = prefs.getString("resultCode", null);
        long timestamp = prefs.getLong("timestamp", 0);
        
        // Chỉ xử lý nếu là result mới (trong 30 giây) và chưa xử lý
        if (resultCode != null && System.currentTimeMillis() - timestamp < 30000) {
            Log.d(TAG, "Found payment result in SharedPreferences: " + resultCode);
            
            // Clear để không xử lý lại
            prefs.edit().clear().apply();
            
            if ("0".equals(resultCode)) {
                // Thanh toán thành công
                Toast.makeText(requireContext(), R.string.txt_payment_success, Toast.LENGTH_SHORT).show();
                processBooking();
            } else {
                // Thanh toán thất bại
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                String message = prefs.getString("message", "Unknown error");
                Toast.makeText(requireContext(), 
                        getString(R.string.txt_payment_failed) + ": " + message, 
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Fragment destroyed");
        
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (momoPaymentReceiver != null) {
            try {
                requireActivity().unregisterReceiver(momoPaymentReceiver);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering receiver", e);
            }
        }
        binding = null;
    }
}
