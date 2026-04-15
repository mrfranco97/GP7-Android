package com.example.xplorenow_android.ui.experience;

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
