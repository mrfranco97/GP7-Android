package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.Experience;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExperienceResponse {
    @SerializedName("items")
    private List<Experience> items;

    public List<Experience> getItems() {
        return items;
    }
}
