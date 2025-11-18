package com.example.ticketapp.utils;

public class Format {
    public static String formatDuration(int totalMinutes) {
        if (totalMinutes <= 0) {
            return "0p";
        }
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        StringBuilder result = new StringBuilder();
        if (hours > 0) {
            result.append("0").append(hours).append("h");
        }
        if (minutes > 0) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(minutes).append("p");
        }
        return result.toString();
    }
}
