package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;

public class CheckinRequest {
    @SerializedName("qr")
    private final String qr;

    public CheckinRequest(String qr) {
        this.qr = qr;
    }
}
