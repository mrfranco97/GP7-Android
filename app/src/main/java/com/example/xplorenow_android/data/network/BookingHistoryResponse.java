package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.BookingHistoryItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingHistoryResponse {
    @SerializedName("items")
    private List<BookingHistoryItem> items;

    public List<BookingHistoryItem> getItems() { return items; }
}
