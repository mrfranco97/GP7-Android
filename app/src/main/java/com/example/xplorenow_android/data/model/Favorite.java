package com.example.xplorenow_android.data.model;


import com.google.gson.annotations.SerializedName;
import java.util.List;


public class Favorite {

    @SerializedName("id")
    private int id;

    @SerializedName("added_at")
    private String addedAt;

    @SerializedName("activity")
    private Activity activity;
    @SerializedName("novelty")
    private Novelty novelty;

    public int getId() { return id; }
    public String getAddedAt() { return addedAt; }
    public Activity getActivity() { return activity; }
    public Novelty getNovelty() { return novelty; }


    public static class Activity {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("destination")
        private String destination;
        @SerializedName("category")
        private String category;
        @SerializedName("category_label")
        private String categoryLabel;
        @SerializedName("interests")
        private List<String> interests;
        @SerializedName("interest_labels")
        private List<String> interestLabels;
        @SerializedName("duration")
        private String duration;
        @SerializedName("price")
        private double price;
        @SerializedName("available_spots")
        private int availableSpots;
        @SerializedName("image_url")
        private String imageUrl;
        @SerializedName("available_date")
        private String availableDate;
        @SerializedName("available_time_slots")
        private List<TimeSlot> availableTimeSlots;
        @SerializedName("quick_booking")
        private QuickBooking quickBooking;
        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getDestination() { return destination; }
        public String getCategory() { return category; }
        public String getCategoryLabel() { return categoryLabel; }
        public List<String> getInterests() { return interests; }
        public List<String> getInterestLabels() { return interestLabels; }
        public String getDuration() { return duration; }
        public double getPrice() { return price; }
        public int getAvailableSpots() { return availableSpots; }
        public String getImageUrl() { return imageUrl; }
        public String getAvailableDate() { return availableDate; }
        public List<TimeSlot> getAvailableTimeSlots() { return availableTimeSlots; }
        public QuickBooking getQuickBooking() { return quickBooking; }
    }
    public static class TimeSlot {
        @SerializedName("time")
        private String time;
        @SerializedName("capacity")
        private int capacity;
        @SerializedName("_id")
        private String id;
        // Getters
        public String getTime() { return time; }
        public int getCapacity() { return capacity; }
        public String getId() { return id; }
    }
    public static class QuickBooking {
        @SerializedName("experience_id")
        private int experienceId;
        @SerializedName("available_date")
        private String availableDate;
        @SerializedName("endpoint")
        private String endpoint;
        // Getters
        public int getExperienceId() { return experienceId; }
        public String getAvailableDate() { return availableDate; }
        public String getEndpoint() { return endpoint; }
    }
    public static class Novelty {
        @SerializedName("has_news")
        private boolean hasNews;
        @SerializedName("price_changed")
        private boolean priceChanged;
        @SerializedName("new_spots_released")
        private boolean newSpotsReleased;
        @SerializedName("previous_price")
        private double previousPrice;
        @SerializedName("current_price")
        private double currentPrice;
        @SerializedName("previous_available_spots")
        private int previousAvailableSpots;
        @SerializedName("current_available_spots")
        private int currentAvailableSpots;
        // Getters
        public boolean hasNews() { return hasNews; }
        public boolean isPriceChanged() { return priceChanged; }
        public boolean isNewSpotsReleased() { return newSpotsReleased; }
        public double getPreviousPrice() { return previousPrice; }
        public double getCurrentPrice() { return currentPrice; }
        public int getPreviousAvailableSpots() { return previousAvailableSpots; }
        public int getCurrentAvailableSpots() { return currentAvailableSpots; }
    }
}
