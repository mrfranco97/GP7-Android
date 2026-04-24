package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Booking {
    @SerializedName("id")
    private Object id;
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
    @SerializedName("can_rate")
    private boolean canRate;
    @SerializedName("rating_available_at")
    private String ratingAvailableAt;
    @SerializedName("rating_window_closes_at")
    private String ratingWindowClosesAt;
    @SerializedName("completed_at")
    private String completedAt;
    @SerializedName("rating")
    private RatingSummary rating;
    @SerializedName("activity")
    private ActivityDetail activity;
    @SerializedName("experience")
    private ExperienceSummary experience;

    public Object getId() { return id; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public int getParticipants() { return participants; }
    public double getTotalPrice() { return totalPrice; }
    public boolean isCanRate() { return canRate; }
    public String getRatingAvailableAt() { return ratingAvailableAt; }
    public String getRatingWindowClosesAt() { return ratingWindowClosesAt; }
    public String getCompletedAt() { return completedAt; }
    public RatingSummary getRating() { return rating; }
    public ActivityDetail getActivity() { return activity; }
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
        @SerializedName("guide_id")
        private String guideId;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDestination() { return destination; }
        public String getImageUrl() { return imageUrl; }
        public String getGuideId() { return guideId; }
    }

    public static class ActivityDetail {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("destination")
        private String destination;
        @SerializedName("description")
        private String description;
        @SerializedName("includes")
        private List<String> includes;
        @SerializedName("meeting_point")
        private String meetingPoint;
        @SerializedName("duration")
        private String duration;
        @SerializedName("language")
        private String language;
        @SerializedName("image_url")
        private String imageUrl;
        @SerializedName("gallery")
        private List<String> gallery;
        @SerializedName("available_date")
        private String availableDate;
        @SerializedName("guide")
        private GuideSummary guide;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDestination() { return destination; }
        public String getDescription() { return description; }
        public List<String> getIncludes() { return includes; }
        public String getMeetingPoint() { return meetingPoint; }
        public String getDuration() { return duration; }
        public String getLanguage() { return language; }
        public String getImageUrl() { return imageUrl; }
        public List<String> getGallery() { return gallery; }
        public String getAvailableDate() { return availableDate; }
        public GuideSummary getGuide() { return guide; }
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
}
