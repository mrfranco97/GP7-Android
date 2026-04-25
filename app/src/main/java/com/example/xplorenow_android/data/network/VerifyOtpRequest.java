package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpRequest {
    @SerializedName("email")
    private final String email;
    @SerializedName("code")
    private final String code;

    public VerifyOtpRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
