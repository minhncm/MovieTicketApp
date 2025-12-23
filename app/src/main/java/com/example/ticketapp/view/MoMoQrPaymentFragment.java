package com.example.ticketapp.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.ticketapp.databinding.FragmentMomoQrPaymentBinding;
import com.example.ticketapp.domain.model.Res.BookingData;
import com.example.ticketapp.utils.MoMoPaymentHelper;
import com.example.ticketapp.viewmodel.BookingViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.NumberFormat;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MoMoQrPaymentFragment extends Fragment {
    
    private FragmentMomoQrPaymentBinding binding;
    private BookingViewModel bookingViewModel;
    private NavController navController;
    private Handler paymentCheckHandler;
    private Runnable paymentCheckRunnable;
    private String currentOrderId;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMomoQrPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        navController = NavHostFragment.findNavController(this);
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        paymentCheckHandler = new Handler();
        
        setupClickListeners();
        initiateMoMoPayment();
    }
    
    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> navController.navigateUp());
        
        binding.btnCancel.setOnClickListener(v -> {
            stopPaymentCheck();
            navController.navigateUp();
        });
    }
    
    private void initiateMoMoPayment() {
        BookingData bookingData = bookingViewModel.bookingDataMutableLiveData.getValue();
        
        if (bookingData == null) {
            Toast.makeText(requireContext(), R.string.msg_booking_missing, Toast.LENGTH_SHORT).show();
            navController.navigateUp();
            return;
        }
        
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.cardQrCode.setVisibility(View.GONE);
        
        // Tính tổng tiền
        long amount = bookingData.getSelectedSeats().size() * 100000L;
        String orderInfo = "Thanh toán vé xem phim - " + bookingData.getSelectedSeats().size() + " ghế";
        
        // Format amount
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        binding.tvAmount.setText("Số tiền: " + formatter.format(amount) + " VND");
        
        MoMoPaymentHelper.createPayment(amount, orderInfo, new MoMoPaymentHelper.MoMoPaymentCallback() {
            @Override
            public void onSuccess(String payUrl, String orderId) {
                requireActivity().runOnUiThread(() -> {
                    currentOrderId = orderId;
                    binding.tvOrderId.setText("Order ID: " + orderId);
                    
                    // Tạo QR code từ payUrl
                    generateQRCode(payUrl);
                    
                    binding.progressBar.setVisibility(View.GONE);
                    binding.cardQrCode.setVisibility(View.VISIBLE);
                    
                    // Bắt đầu check payment status (giả lập)
                    startPaymentCheck();
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), 
                            getString(R.string.txt_momo_payment_error) + ": " + errorMessage, 
                            Toast.LENGTH_LONG).show();
                    navController.navigateUp();
                });
            }
        });
    }
    
    private void generateQRCode(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            
            binding.ivQrCode.setImageBitmap(bitmap);
            
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi tạo QR code", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Giả lập check payment status
     * Trong thực tế, cần implement IPN callback từ MoMo hoặc polling API
     */
    private void startPaymentCheck() {
        paymentCheckRunnable = new Runnable() {
            int checkCount = 0;
            
            @Override
            public void run() {
                checkCount++;
                
                // Giả lập: Sau 10 giây (5 lần check) thì coi như thanh toán thành công
                if (checkCount >= 5) {
                    onPaymentSuccess();
                    return;
                }
                
                // Check lại sau 2 giây
                binding.tvStatus.setText("Đang chờ thanh toán... (" + (checkCount * 2) + "s)");
                paymentCheckHandler.postDelayed(this, 2000);
            }
        };
        
        paymentCheckHandler.postDelayed(paymentCheckRunnable, 2000);
    }
    
    private void stopPaymentCheck() {
        if (paymentCheckHandler != null && paymentCheckRunnable != null) {
            paymentCheckHandler.removeCallbacks(paymentCheckRunnable);
        }
    }
    
    private void onPaymentSuccess() {
        binding.tvStatus.setText("✓ Thanh toán thành công!");
        binding.tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light, null));
        
        // Process booking
        BookingData bookingData = bookingViewModel.bookingDataMutableLiveData.getValue();
        if (bookingData != null) {
            bookingViewModel.bookingTicket(bookingData).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null) {
                    switch (resource.getStatus()) {
                        case SUCCESS:
                            // Tạo ticket và navigate
                            com.example.ticketapp.domain.model.Ticket ticket = 
                                    new com.example.ticketapp.domain.model.Ticket(
                                            resource.getData().getMovieName(),
                                            resource.getData().getCinemaName(),
                                            resource.getData().getSeatNames(),
                                            resource.getData().getShowStartTime(),
                                            resource.getData().getStatus(),
                                            resource.getData().getId(),
                                            resource.getData().getTotalPrice()
                                    );
                            bookingViewModel.setCurrentTicket(ticket);
                            
                            // Delay 1 giây để user thấy success message
                            new Handler().postDelayed(() -> {
                                navController.navigate(R.id.action_momoQrPaymentFragment_to_paymentSuccessDialog);
                            }, 1000);
                            break;
                        case ERROR:
                            Toast.makeText(requireContext(), 
                                    "Lỗi đặt vé: " + resource.getMessage(), 
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPaymentCheck();
        binding = null;
    }
}
