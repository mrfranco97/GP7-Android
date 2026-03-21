package com.example.xplorenow_android;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExperienceResponse {
    @SerializedName("items")
    private List<ActivityItem> items;

    public List<ActivityItem> getItems() {
        return items;
    }
}
