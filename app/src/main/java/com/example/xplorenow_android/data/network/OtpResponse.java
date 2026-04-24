package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;

public class OtpResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("expires_in_minutes")
    private int expiresInMinutes;

    public String getMessage() { return message; }
    public int getExpiresInMinutes() { return expiresInMinutes; }
}
