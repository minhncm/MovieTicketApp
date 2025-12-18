package com.example.ticketapp.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class VietnameseUtils {
    
    /**
     * Chuyển chuỗi tiếng Việt có dấu thành không dấu
     * Ví dụ: "Sài Gòn Rực Lửa" → "sai gon ruc lua"
     */
    public static String removeVietnameseAccents(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        try {
            // Normalize Unicode
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            temp = pattern.matcher(temp).replaceAll("");
            
            // Thay thế các ký tự đặc biệt tiếng Việt
            temp = temp.replace('đ', 'd').replace('Đ', 'D');
            
            return temp.toLowerCase();
        } catch (Exception e) {
            return str.toLowerCase();
        }
    }
    
    /**
     * So sánh 2 chuỗi tiếng Việt không phân biệt dấu
     * Ví dụ: contains("Sài Gòn", "sai gon") → true
     */
    public static boolean containsIgnoreAccents(String source, String search) {
        if (source == null || search == null) {
            return false;
        }
        
        String normalizedSource = removeVietnameseAccents(source);
        String normalizedSearch = removeVietnameseAccents(search);
        
        return normalizedSource.contains(normalizedSearch);
    }
}
