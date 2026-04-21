package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.Booking;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MyBookingsResponse {
    @SerializedName("items")
    private List<Booking> items;

    public List<Booking> getItems() { return items; }
}
