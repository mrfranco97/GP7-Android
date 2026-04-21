package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
    @SerializedName("experienceId")
    private String experienceId;
    @SerializedName("date")
    private String date;
    @SerializedName("timeSlot")
    private String timeSlot;
    @SerializedName("participants")
    private int participants;

    public BookingRequest(String experienceId, String date, String timeSlot, int participants) {
        this.experienceId = experienceId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.participants = participants;
    }
}
