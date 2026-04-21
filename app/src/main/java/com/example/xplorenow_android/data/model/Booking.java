package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;

public class Booking {
    @SerializedName("id")
    private Object id; // Can be String or Integer
    @SerializedName("status")
    private String status;
    @SerializedName("raw_status")
    private String rawStatus;
    @SerializedName("date")
    private String date;
    @SerializedName("time_slot")
    private String timeSlot;
    @SerializedName("participants")
    private int participants;
    @SerializedName("total_price")
    private double totalPrice;
    @SerializedName("experience")
    private ExperienceSummary experience;

    public Object getId() { return id; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public int getParticipants() { return participants; }
    public double getTotalPrice() { return totalPrice; }
    public ExperienceSummary getExperience() { return experience; }

    public static class ExperienceSummary {
        @SerializedName("id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("destination")
        private String destination;
        @SerializedName("image_url")
        private String imageUrl;

        public String getName() { return name; }
        public String getDestination() { return destination; }
        public String getImageUrl() { return imageUrl; }
    }
}
