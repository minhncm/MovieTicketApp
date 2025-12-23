package com.example.ticketapp.domain.repository;

import com.example.ticketapp.domain.model.MovieReview;
import java.util.List;

public interface MovieReviewRepository {
    
    /**
     * Lấy danh sách review theo movieId
     */
    List<MovieReview> getReviewsByMovie(String movieId);
    
    /**
     * Lấy review của user cho một phim cụ thể
     */
    MovieReview getUserReviewForMovie(String movieId, String userId);
    
    /**
     * Thêm review mới
     */
    boolean addReview(MovieReview review);
    
    /**
     * Cập nhật review
     */
    boolean updateReview(MovieReview review);
    
    /**
     * Xóa review
     */
    boolean deleteReview(String reviewId);
    
    /**
     * Tính điểm trung bình của phim
     */
    float getAverageRating(String movieId);
    
    /**
     * Đếm số lượng review của phim
     */
    int getReviewCount(String movieId);
}
