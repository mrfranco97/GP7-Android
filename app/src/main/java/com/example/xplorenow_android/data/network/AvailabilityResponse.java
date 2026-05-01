package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AvailabilityResponse {
    @SerializedName("experience_id")
    private int experienceId;
    
    @SerializedName("dates")
    private List<AvailabilityDate> dates;

    public int getExperienceId() { return experienceId; }
    public List<AvailabilityDate> getDates() { return dates; }

    public static class AvailabilityDate {
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

    public static class TimeSlot {
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
}
