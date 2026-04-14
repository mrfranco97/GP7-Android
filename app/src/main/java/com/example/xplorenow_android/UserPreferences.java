package com.example.xplorenow_android;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserPreferences {
    @SerializedName("travelInterests")
    private List<String> travelInterests;

    public UserPreferences(List<String> travelInterests) {
        this.travelInterests = travelInterests;
    }

    public List<String> getTravelInterests() { return travelInterests; }
    public void setTravelInterests(List<String> travelInterests) { this.travelInterests = travelInterests; }
}
