package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Experience {
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
    @SerializedName("duration")
    private String duration;
    @SerializedName("price")
    private double price;
    @SerializedName("available_spots")
    private int availableSpots;
    @SerializedName("image_url")
    private String imageUrl;
    
    // Detail fields
    @SerializedName("description")
    private String description;
    @SerializedName("includes")
    private List<String> includes;
    @SerializedName("meeting_point")
    private String meetingPoint;
    @SerializedName("assigned_guide")
    private Guide assignedGuide;
    @SerializedName("language")
    private String language;
    @SerializedName("cancellation_policy")
    private String cancellationPolicy;
    @SerializedName("gallery")
    private List<String> gallery;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDestination() { return destination; }
    public String getCategory() { return category; }
    public String getCategoryLabel() { return categoryLabel; }
    public String getDuration() { return duration; }
    public double getPrice() { return price; }
    public int getAvailableSpots() { return availableSpots; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public List<String> getIncludes() { return includes; }
    public String getMeetingPoint() { return meetingPoint; }
    public Guide getAssignedGuide() { return assignedGuide; }
    public String getLanguage() { return language; }
    public String getCancellationPolicy() { return cancellationPolicy; }
    public List<String> getGallery() { return gallery; }
}
