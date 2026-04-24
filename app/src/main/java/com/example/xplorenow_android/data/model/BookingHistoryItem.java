package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;

public class BookingHistoryItem {
    @SerializedName("id")
    private long id;
    
    @SerializedName("date")
    private String date;
    
    @SerializedName("activity")
    private ActivitySummary activity;
    
    @SerializedName("guide")
    private GuideSummary guide;
    
    @SerializedName("rating")
    private RatingSummary rating;
    
    @SerializedName("detail")
    private Detail detail;

    public long getId() { return id; }
    public String getDate() { return date; }
    public ActivitySummary getActivity() { return activity; }
    public GuideSummary getGuide() { return guide; }
    public RatingSummary getRating() { return rating; }
    public Detail getDetail() { return detail; }

    public static class ActivitySummary {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("destination")
        private String destination;
        @SerializedName("duration")
        private String duration;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDestination() { return destination; }
        public String getDuration() { return duration; }
    }

    public static class GuideSummary {
        @SerializedName("id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("email")
        private String email;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }

    public static class RatingSummary {
        @SerializedName("activity_stars")
        private int activityStars;
        @SerializedName("guide_stars")
        private int guideStars;
        @SerializedName("comment")
        private String comment;
        @SerializedName("submitted_at")
        private String submittedAt;

        public int getActivityStars() { return activityStars; }
        public int getGuideStars() { return guideStars; }
        public String getComment() { return comment; }
        public String getSubmittedAt() { return submittedAt; }
    }

    public static class Detail {
        @SerializedName("booking_id")
        private long bookingId;
        @SerializedName("activity_id")
        private int activityId;

        public long getBookingId() { return bookingId; }
        public int getActivityId() { return activityId; }
    }
}
