package com.example.ticketapp.data.repository;

import com.example.ticketapp.domain.model.MovieReview;
import com.example.ticketapp.domain.repository.MovieReviewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MovieReviewRepositoryImpl implements MovieReviewRepository {
    
    // Mock data - sau này sẽ thay bằng API call
    private final List<MovieReview> mockReviews;
    
    @Inject
    public MovieReviewRepositoryImpl() {
        this.mockReviews = new ArrayList<>();
        initMockData();
    }
    
    private void initMockData() {
        // Thêm một số review mẫu
        mockReviews.add(new MovieReview(
            UUID.randomUUID().toString(),
            "1", // movieId
            "user1",
            "Nguyễn Văn A",
            null,
            4.5f,
            "Phim rất hay, diễn xuất tốt, cốt truyện hấp dẫn!",
            System.currentTimeMillis() - 86400000 // 1 ngày trước
        ));
        
        mockReviews.add(new MovieReview(
            UUID.randomUUID().toString(),
            "1",
            "user2",
            "Trần Thị B",
            null,
            5.0f,
            "Xuất sắc! Đáng xem nhất năm nay.",
            System.currentTimeMillis() - 172800000 // 2 ngày trước
        ));
        
        mockReviews.add(new MovieReview(
            UUID.randomUUID().toString(),
            "1",
            "user3",
            "Lê Văn C",
            null,
            3.5f,
            "Phim ổn, nhưng hơi dài dòng một chút.",
            System.currentTimeMillis() - 259200000 // 3 ngày trước
        ));
    }

    @Override
    public List<MovieReview> getReviewsByMovie(String movieId) {
        return mockReviews.stream()
                .filter(review -> review.getMovieId().equals(movieId))
                .collect(Collectors.toList());
    }

    @Override
    public MovieReview getUserReviewForMovie(String movieId, String userId) {
        return mockReviews.stream()
                .filter(review -> review.getMovieId().equals(movieId) 
                        && review.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean addReview(MovieReview review) {
        // Tạo ID mới nếu chưa có
        if (review.getId() == null || review.getId().isEmpty()) {
            review.setId(UUID.randomUUID().toString());
        }
        
        // Set timestamp nếu chưa có
        if (review.getTimestamp() == 0) {
            review.setTimestamp(System.currentTimeMillis());
        }
        
        mockReviews.add(review);
        return true;
    }

    @Override
    public boolean updateReview(MovieReview review) {
        for (int i = 0; i < mockReviews.size(); i++) {
            if (mockReviews.get(i).getId().equals(review.getId())) {
                mockReviews.set(i, review);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteReview(String reviewId) {
        return mockReviews.removeIf(review -> review.getId().equals(reviewId));
    }

    @Override
    public float getAverageRating(String movieId) {
        List<MovieReview> reviews = getReviewsByMovie(movieId);
        if (reviews.isEmpty()) {
            return 0f;
        }
        
        float sum = 0;
        for (MovieReview review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }

    @Override
    public int getReviewCount(String movieId) {
        return getReviewsByMovie(movieId).size();
    }
}
