package com.example.ticketapp.domain.model;

import com.google.gson.annotations.SerializedName;

public class Account {
        @SerializedName("uid")
        private String uid;
        
        @SerializedName(value = "username", alternate = {"displayName"})
        private String username;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("posterUrl")
        private String posterUrl;
        
        @SerializedName("sdt")
        private String phoneNumber;
        
        @SerializedName("diaChi")
        private String address;
        
        @SerializedName("gioiTinh")
        private String gender;
        
        @SerializedName("thanhVienTu")
        private String memberSince;
        
        @SerializedName("tongVeDaMua")
        private int totalTickets;
        
        @SerializedName("diemThuong")
        private int rewardPoints;
    
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
