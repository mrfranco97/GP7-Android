package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.TimeSlot;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AvailabilityResponse {
    @SerializedName("experience_id")
    private int experienceId;
    @SerializedName("date")
    private String date;
    @SerializedName("slots")
    private List<TimeSlot> slots;

    public int getExperienceId() { return experienceId; }
    public String getDate() { return date; }
    public List<TimeSlot> getSlots() { return slots; }
}
