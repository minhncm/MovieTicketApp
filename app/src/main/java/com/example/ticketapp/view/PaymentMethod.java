package com.example.ticketapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ticketapp.R;
import com.example.ticketapp.adapter.CardAdapter;
import com.example.ticketapp.databinding.FragmentPaymentMethodBinding;
import com.example.ticketapp.domain.model.PaymentCard;
import com.example.ticketapp.domain.model.Res.BookingData;
import com.example.ticketapp.domain.model.Res.BookingRes;
import com.example.ticketapp.domain.model.Ticket;
import com.example.ticketapp.utils.Resource;
import com.example.ticketapp.view.dialog.PaymentSuccessDialog;
import com.example.ticketapp.viewmodel.BookingViewModel;
import com.example.ticketapp.viewmodel.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class PaymentMethod extends Fragment {

    private FragmentPaymentMethodBinding binding;
    private RecyclerView recyclerViewPaymentMethods;
    private BookingViewModel bookingViewModel;
    private CardAdapter cardAdapter ;
    private String userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPaymentMethodBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewPaymentMethods = binding.recyclerViewCards;
        setUpViewModel();
        List<PaymentCard> cardList = List.of(
                new PaymentCard("1234 5678 9012 3456", "John Doe", "12/24","123"),
                new PaymentCard("9876 5432 1098 7654", "Jane Smith", "11/23","456"),
                new PaymentCard("4567 8901 2345 6789", "Alice Johnson", "10/25","789")
        );
        cardAdapter = new CardAdapter(cardList);
        recyclerViewPaymentMethods.setAdapter(cardAdapter);
        binding.layoutPayButton.setOnClickListener(v -> {
            BookingData bookingData = bookingViewModel.bookingDataMutableLiveData.getValue();
            if (bookingData != null) {
                bookingViewModel.bookingTicket(
                       bookingData
                ).observe(getViewLifecycleOwner(),resource -> {
                    if (resource != null) {
                        switch (resource.getStatus()) {
                            case LOADING:
//                        showLoadingIndicator();
                                break;
                            case SUCCESS:
                                // 2. Xử lý thành công
//                        hideLoadingIndicator();
                                Ticket ticket = new Ticket(
                                        resource.getData().getMovieName(),
                                        resource.getData().getCinemaName(),
                                        resource.getData().getSeatNames(),
                                        resource.getData().getShowStartTime(),
                                        resource.getData().getStatus(),
                                        resource.getData().getId(),
                                        resource.getData().getTotalPrice())
                                        ;
                                bookingViewModel.setCurrentTicket(ticket);
                                NavController navController = NavHostFragment.findNavController(this);
                                navController.navigate(PaymentMethodDirections.actionPaymentMethodToPaymentSuccessDialog2());
//                        showSuccessMessage(result);
                                break;
                            case ERROR:
                                // 3. Xử lý lỗi
//                        hideLoadingIndicator();
                                String errorMessage = resource.getMessage();
                                // Hiển thị thông báo lỗi
//                        showErrorMessage(errorMessage);
                                break;
                        }
                    }
                });
            } else {
                Toast.makeText(requireContext(), getString(R.string.msg_booking_missing), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpViewModel() {
        bookingViewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
    }

}