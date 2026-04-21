package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;

public class BookingResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("status")
    private String status;
    @SerializedName("total_price")
    private double totalPrice;
    @SerializedName("message")
    private String message;
    @SerializedName("available_spots")
    private Integer availableSpots;

    public String getId() { return id; }
    public String getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public String getMessage() { return message; }
    public Integer getAvailableSpots() { return availableSpots; }
}
