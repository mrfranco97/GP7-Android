package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;

public class NotificationItem {
    private String id;
    private String type; // reminder | reschedule | cancellation
    private String title;
    private String body;
    @SerializedName("created_at")
    private String createdAt;
    private boolean read;
    private NotificationData data;

    // Getters and Setters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getCreatedAt() { return createdAt; }
    public boolean isRead() { return read; }
    public NotificationData getData() { return data; }

    public static class NotificationData {
        @SerializedName("booking_id")
        private int bookingId;
        @SerializedName("experience_id")
        private int experienceId;
        @SerializedName("activity_name")
        private String activityName;
        private String date;
        @SerializedName("time_slot")
        private String timeSlot;
        @SerializedName("voucher_url")
        private String voucherUrl;
        @SerializedName("new_date")
        private String newDate;
        @SerializedName("new_time_slot")
        private String newTimeSlot;

        // Getters
        public int getBookingId() { return bookingId; }
        public int getExperienceId() { return experienceId; }
        public String getActivityName() { return activityName; }
        public String getDate() { return date; }
        public String getTimeSlot() { return timeSlot; }
        public String getVoucherUrl() { return voucherUrl; }
        public String getNewDate() { return newDate; }
        public String getNewTimeSlot() { return newTimeSlot; }
    }
}
