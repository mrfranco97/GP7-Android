package com.example.xplorenow_android.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.util.List;

@Entity(tableName = "bookings")
public class Booking {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;
    
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

    // Field to track offline changes
    private boolean isPendingCancellation = false;

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRawStatus() { return rawStatus; }
    public void setRawStatus(String rawStatus) { this.rawStatus = rawStatus; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public int getParticipants() { return participants; }
    public void setParticipants(int participants) { this.participants = participants; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public boolean isCanRate() { return canRate; }
    public void setCanRate(boolean canRate) { this.canRate = canRate; }

    public String getRatingAvailableAt() { return ratingAvailableAt; }
    public void setRatingAvailableAt(String ratingAvailableAt) { this.ratingAvailableAt = ratingAvailableAt; }

    public String getRatingWindowClosesAt() { return ratingWindowClosesAt; }
    public void setRatingWindowClosesAt(String ratingWindowClosesAt) { this.ratingWindowClosesAt = ratingWindowClosesAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public RatingSummary getRating() { return rating; }
    public void setRating(RatingSummary rating) { this.rating = rating; }

    public ActivityDetail getActivity() { return activity; }
    public void setActivity(ActivityDetail activity) { this.activity = activity; }

    public ExperienceSummary getExperience() { return experience; }
    public void setExperience(ExperienceSummary experience) { this.experience = experience; }

    public boolean isPendingCancellation() { return isPendingCancellation; }
    public void setPendingCancellation(boolean pendingCancellation) { isPendingCancellation = pendingCancellation; }

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
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getGuideId() { return guideId; }
        public void setGuideId(String guideId) { this.guideId = guideId; }
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
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getIncludes() { return includes; }
        public void setIncludes(List<String> includes) { this.includes = includes; }
        public String getMeetingPoint() { return meetingPoint; }
        public void setMeetingPoint(String meetingPoint) { this.meetingPoint = meetingPoint; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public List<String> getGallery() { return gallery; }
        public void setGallery(List<String> gallery) { this.gallery = gallery; }
        public String getAvailableDate() { return availableDate; }
        public void setAvailableDate(String availableDate) { this.availableDate = availableDate; }
        public GuideSummary getGuide() { return guide; }
        public void setGuide(GuideSummary guide) { this.guide = guide; }
    }

    public static class GuideSummary {
        @SerializedName("id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("email")
        private String email;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
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
        public void setActivityStars(int activityStars) { this.activityStars = activityStars; }
        public int getGuideStars() { return guideStars; }
        public void setGuideStars(int guideStars) { this.guideStars = guideStars; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public String getSubmittedAt() { return submittedAt; }
        public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    }
}
