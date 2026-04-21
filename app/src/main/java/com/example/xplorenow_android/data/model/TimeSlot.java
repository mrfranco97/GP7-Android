package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;

public class TimeSlot {
    @SerializedName("time")
    private String time;
    @SerializedName("capacity")
    private int capacity;
    @SerializedName("reserved_spots")
    private int reservedSpots;
    @SerializedName("available_spots")
    private int availableSpots;

    public String getTime() { return time; }
    public int getCapacity() { return capacity; }
    public int getReservedSpots() { return reservedSpots; }
    public int getAvailableSpots() { return availableSpots; }
}
