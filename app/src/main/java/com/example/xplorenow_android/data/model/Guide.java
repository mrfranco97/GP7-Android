package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;

public class Guide {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
