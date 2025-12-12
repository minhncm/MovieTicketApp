package com.example.ticketapp.domain.model;

public class Account {
        private String uid;
        private String username;
        private String email;
        private String posterUrl;
        private String phoneNumber;
    
    public Account() {
    }

    public Account(String userId, String name, String email) {

    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Account(String uid, String username, String email,String posterUrl) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.posterUrl = posterUrl;

    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
