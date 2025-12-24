package com.example.ticketapp.domain.model.Res;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("uid")
    private String uid;

    public boolean isSuccess() {
        // Backend trả về message chứa "thành công" khi update thành công
        return message != null && message.toLowerCase().contains("thành công");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
