package com.example.ticketapp.view.Movie;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketapp.R;
import com.example.ticketapp.adapter.MovieReviewAdapter;
import com.example.ticketapp.domain.model.MovieReview;
import com.example.ticketapp.viewmodel.MovieReviewViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieReviewFragment extends Fragment {
    
    private MovieReviewViewModel viewModel;
    private MovieReviewAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvAverageRating;
    private TextView tvReviewCount;
    private RatingBar ratingBarAverage;
    private FloatingActionButton fabAddReview;
    
    private String movieId;
    private String currentUserId;
    
    public static MovieReviewFragment newInstance(String movieId) {
        MovieReviewFragment fragment = new MovieReviewFragment();
        Bundle args = new Bundle();
        args.putString("movieId", movieId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getString("movieId");
        }
        
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_review, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewReviews);
        tvAverageRating = view.findViewById(R.id.tvAverageRating);
        tvReviewCount = view.findViewById(R.id.tvReviewCount);
        ratingBarAverage = view.findViewById(R.id.ratingBarAverage);
        fabAddReview = view.findViewById(R.id.fabAddReview);
        
        // Nút back
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().finish());
        }
    }
    
    private void setupRecyclerView() {
        adapter = new MovieReviewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        adapter.setOnReviewClickListener(new MovieReviewAdapter.OnReviewClickListener() {
            @Override
            public void onReviewClick(MovieReview review) {
                // Xem chi tiết review
            }
            
            @Override
            public void onReviewLongClick(MovieReview review) {
                // Nếu là review của user hiện tại, cho phép sửa/xóa
                if (review.getUserId().equals(currentUserId)) {
                    showEditDeleteDialog(review);
                }
            }
        });
    }
    
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MovieReviewViewModel.class);
        
        viewModel.getReviewsLiveData().observe(getViewLifecycleOwner(), reviews -> {
            adapter.setReviews(reviews);
        });
        
        viewModel.getAverageRatingLiveData().observe(getViewLifecycleOwner(), avgRating -> {
            tvAverageRating.setText(String.format("%.1f", avgRating));
            ratingBarAverage.setRating(avgRating);
        });
        
        viewModel.getReviewCountLiveData().observe(getViewLifecycleOwner(), count -> {
            tvReviewCount.setText(count + " đánh giá");
        });
        
        viewModel.getOperationSuccessLiveData().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
        
        // Load reviews
        viewModel.loadReviewsByMovie(movieId);
    }
    
    private void setupListeners() {
        fabAddReview.setOnClickListener(v -> showAddReviewDialog());
    }
    
    private void showAddReviewDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_review, null);
        
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText etComment = dialogView.findViewById(R.id.etComment);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = etComment.getText().toString().trim();
            
            if (comment.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
                return;
            }
            
            MovieReview review = new MovieReview(
                    null,
                    movieId,
                    currentUserId,
                    "User", // Có thể lấy tên từ Firebase
                    null,
                    rating,
                    comment,
                    System.currentTimeMillis()
            );
            
            viewModel.addReview(review);
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private void showEditDeleteDialog(MovieReview review) {
        new AlertDialog.Builder(getContext())
                .setTitle("Tùy chọn")
                .setItems(new String[]{"Sửa", "Xóa"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditReviewDialog(review);
                    } else {
                        showDeleteConfirmDialog(review);
                    }
                })
                .show();
    }
    
    private void showEditReviewDialog(MovieReview review) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_review, null);
        
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText etComment = dialogView.findViewById(R.id.etComment);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        
        // Set giá trị hiện tại
        ratingBar.setRating(review.getRating());
        etComment.setText(review.getComment());
        btnSubmit.setText("Cập nhật");
        
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSubmit.setOnClickListener(v -> {
            review.setRating(ratingBar.getRating());
            review.setComment(etComment.getText().toString().trim());
            
            viewModel.updateReview(review);
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private void showDeleteConfirmDialog(MovieReview review) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa đánh giá này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteReview(review.getId(), movieId);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
