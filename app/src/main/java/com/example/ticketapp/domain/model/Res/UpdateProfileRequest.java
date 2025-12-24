package com.example.ticketapp.domain.model.Res;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("uid")
    private String uid;

    @SerializedName("email")
    private String email;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("sdt")
    private String phoneNumber;

    @SerializedName("gioiTinh")
    private String gender;

    @SerializedName("diaChi")
    private String address;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
