package com.example.ticketapp.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketapp.databinding.ItemPaymentCardBinding;
import com.example.ticketapp.domain.model.PaymentCard;

import java.util.List;

public class CardAdapter  extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<PaymentCard> cardList;
    private ItemPaymentCardBinding binding;

    public  CardAdapter (List<PaymentCard> _cardList){
        this.cardList = _cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemPaymentCardBinding.inflate(
                android.view.LayoutInflater.from(parent.getContext()), parent, false
        );
        return new CardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.onBind(cardList.get(position));
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public  static  class  CardViewHolder extends RecyclerView.ViewHolder {

        private final ItemPaymentCardBinding binding;
        public CardViewHolder( ItemPaymentCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public   void onBind (PaymentCard paymentCard){
            binding.expireDate.setText(paymentCard.getExpiryDate());
            binding.textCardNumber.setText(paymentCard.getCardNumber());
            binding.textCardHolder.setText(paymentCard.getCardHolderName());



        }
    }
}
