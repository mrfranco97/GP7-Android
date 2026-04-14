package com.example.xplorenow_android;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InterestResponse {
    @SerializedName("items")
    private List<Interest> items;

    public List<Interest> getItems() { return items; }
}
