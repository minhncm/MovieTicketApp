package com.example.ticketapp.utils;


import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView.OnScrollListener tùy chỉnh để áp dụng hiệu ứng phóng to/thu nhỏ
 * (Transform Scale) cho item nằm gần trung tâm màn hình nhất (Center Card Effect).
 */
public class CenterItemScrollListener extends RecyclerView.OnScrollListener {

    private static final float MIN_SCALE = 0.85f; // Tỷ lệ thu nhỏ tối thiểu
    private static final float MIN_ALPHA = 0.6f;  // Độ mờ tối thiểu

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // Lấy tâm của RecyclerView (nơi thẻ nổi bật nhất sẽ xuất hiện)
        int center = recyclerView.getWidth() / 2;

        // Kiểm tra từng item View
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            int childCenter = (child.getLeft() + child.getRight()) / 2;
            int childWidth = child.getWidth();

            // Tính khoảng cách từ tâm item đến tâm RecyclerView
            // Giá trị tuyệt đối của khoảng cách (càng xa tâm, giá trị càng lớn)
            float distance = Math.abs(center - childCenter);

            // Tính tỷ lệ biến đổi (scaleFactor) dựa trên khoảng cách
            // Ta chia cho một hằng số (childWidth * 2) để kiểm soát độ biến đổi
            float scale = 1 - (distance / (childWidth * 2));
            float scaleFactor = Math.max(MIN_SCALE, scale);

            // Áp dụng Scale
            child.setScaleX(scaleFactor);
            child.setScaleY(scaleFactor);

            // Áp dụng Alpha (độ trong suốt)
            // Tính toán alpha tuyến tính giữa MIN_ALPHA và 1.0
            float alphaFactor = MIN_ALPHA + (scaleFactor - MIN_SCALE) * (1 - MIN_ALPHA) / (1 - MIN_SCALE);
            child.setAlpha(alphaFactor);
        }
    }
}