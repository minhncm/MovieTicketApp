package com.example.ticketapp.domain.model.Res;

import com.example.ticketapp.domain.model.Account;

public class AuthResult {
    private boolean success;      // Thành công hay thất bại?
    private String message;
    private Account user;// Thông báo gì?

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthResult(boolean success, String message, Account user) {
        this.success = success;
        this.user = user;
        this.message = message;
    }

    public AuthResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

              // Dữ liệu user (nếu có)
}