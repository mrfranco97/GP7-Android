package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;

public class OtpRequest {
    @SerializedName("email")
    private final String email;

    public OtpRequest(String email) {
        this.email = email;
    }
}
