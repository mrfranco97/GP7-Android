package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;

public class Interest {
    @SerializedName("key")
    private String key;
    @SerializedName("label")
    private String label;

    public String getKey() { return key; }
    public String getLabel() { return label; }
}
