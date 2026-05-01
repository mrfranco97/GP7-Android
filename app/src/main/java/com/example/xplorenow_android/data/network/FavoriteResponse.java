package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;
import com.example.xplorenow_android.data.model.Favorite;
import java.util.List;

public class FavoriteResponse {
    @SerializedName("items")
    private List<Favorite> items;

    public List<Favorite> getItems() { return items; }
}
