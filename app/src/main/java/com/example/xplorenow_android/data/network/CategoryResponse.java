package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoryResponse {
    @SerializedName("items")
    private List<Category> items;

    public List<Category> getItems() { return items; }
}
