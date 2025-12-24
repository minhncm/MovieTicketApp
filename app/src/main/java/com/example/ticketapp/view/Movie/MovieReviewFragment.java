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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketapp.R;
import com.example.ticketapp.adapter.MovieReviewAdapter;
import com.example.ticketapp.domain.model.MovieReview;
import com.example.ticketapp.viewmodel.MovieReviewViewModel;
import com.example.ticketapp.viewmodel.MovieViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieReviewFragment extends Fragment {
    
    private MovieReviewViewModel reviewViewModel;
    private MovieViewModel movieViewModel;
    private MovieReviewAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvAverageRating;
    private TextView tvReviewCount;
    private RatingBar ratingBarAverage;
    private FloatingActionButton fabAddReview;
    
    private String movieId;
    private String bookingId;
    private String currentUserId;
    private boolean hasWatchedMovie = false;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Lấy arguments từ SafeArgs
        if (getArguments() != null) {
            MovieReviewFragmentArgs args = MovieReviewFragmentArgs.fromBundle(getArguments());
            hasWatchedMovie = args.getHasWatchedMovie();
            bookingId = args.getBookingId();
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
    
    private void setupViewModel() {
        reviewViewModel = new ViewModelProvider(this).get(MovieReviewViewModel.class);
        movieViewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
        
        // Lấy movieId từ MovieViewModel
        movieViewModel.selectedMovie.observe(getViewLifecycleOwner(), movie -> {
            if (movie != null && movieId == null) {
                movieId = movie.getId();
                // Load reviews khi có movieId
                if (movieId != null) {
                    reviewViewModel.loadReviewsByMovie(movieId);
                }
            }
        });
        
        reviewViewModel.getReviewsLiveData().observe(getViewLifecycleOwner(), reviews -> {
            adapter.setReviews(reviews);
        });
        
        reviewViewModel.getAverageRatingLiveData().observe(getViewLifecycleOwner(), avgRating -> {
            tvAverageRating.setText(String.format("%.1f", avgRating));
            ratingBarAverage.setRating(avgRating);
        });
        
        reviewViewModel.getReviewCountLiveData().observe(getViewLifecycleOwner(), count -> {
            tvReviewCount.setText(count + " đánh giá");
        });
        
        reviewViewModel.getOperationSuccessLiveData().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        });
        
        reviewViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
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
            btnBack.setOnClickListener(v -> {
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigateUp();
            });
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
    
    private void setupListeners() {
        // Chỉ cho phép đánh giá nếu đã xem phim
        if (hasWatchedMovie) {
            fabAddReview.setVisibility(View.VISIBLE);
            fabAddReview.setOnClickListener(v -> showAddReviewDialog());
        } else {
            fabAddReview.setVisibility(View.GONE);
        }
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
            
            reviewViewModel.addReview(review, bookingId != null ? bookingId : "");
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
            
            reviewViewModel.updateReview(review);
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private void showDeleteConfirmDialog(MovieReview review) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa đánh giá này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    reviewViewModel.deleteReview(review.getId(), currentUserId, movieId);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
