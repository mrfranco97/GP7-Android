package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("key")
    private String key;
    @SerializedName("label")
    private String label;

    public String getKey() { return key; }
    public String getLabel() { return label; }
}
