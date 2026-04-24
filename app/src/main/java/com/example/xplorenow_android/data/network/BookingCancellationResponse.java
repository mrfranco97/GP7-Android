package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;

public class BookingCancellationResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("status")
    private String status;
    @SerializedName("cancellation_policy")
    private String cancellationPolicy;
    @SerializedName("total_price")
    private double totalPrice;
    @SerializedName("cancellation_fee")
    private double cancellationFee;
    @SerializedName("refund_amount")
    private double refundAmount;
    @SerializedName("message")
    private String message;

    public String getId() { return id; }
    public String getStatus() { return status; }
    public String getCancellationPolicy() { return cancellationPolicy; }
    public double getTotalPrice() { return totalPrice; }
    public double getCancellationFee() { return cancellationFee; }
    public double getRefundAmount() { return refundAmount; }
    public String getMessage() { return message; }
}
