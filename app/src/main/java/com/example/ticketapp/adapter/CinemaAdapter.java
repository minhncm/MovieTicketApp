package com.example.ticketapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketapp.R;
import com.example.ticketapp.databinding.ItemCinemaBinding;
import com.example.ticketapp.domain.model.Cinema;
import com.bumptech.glide.Glide; // For loading images

import java.util.ArrayList;
import java.util.List;

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.CinemaViewHolder> {
    private List<Cinema> cinemaList;
    private ItemCinemaBinding binding;

    public  CinemaAdapter(){
cinemaList = new ArrayList<>();
    }
    public  void updateListCinema (List<Cinema> _listCinema){
        cinemaList.addAll(_listCinema);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CinemaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCinemaBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false);
        return new CinemaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CinemaViewHolder holder, int position) {
        holder.bind(cinemaList.get(position) );
    }

    @Override
    public int getItemCount() {
        return cinemaList.size();

    }

    static class CinemaViewHolder extends RecyclerView.ViewHolder {

        private  final  ItemCinemaBinding binding;


        CinemaViewHolder(@NonNull ItemCinemaBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind (Cinema cinema){
            binding.tvCinemaName.setText(cinema.getName());
            binding.tvCinemaInfo.setText(cinema.getInfo());
            binding.tvCinemaRating.setText(String.valueOf(cinema.getRating()));
            Glide.with(binding.getRoot().getContext())
                    .load(R.drawable.img_cinema)
                    .into(binding.ivCinemaLogo);
        }
    }
}