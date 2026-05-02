package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.News;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponse {
    @SerializedName("items")
    private List<News> items;

    public List<News> getItems() {
        return items;
    }
}
