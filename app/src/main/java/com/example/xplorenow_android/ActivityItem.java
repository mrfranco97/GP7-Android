package com.example.xplorenow_android;

import com.google.gson.annotations.SerializedName;

public class ActivityItem {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("destination")
    private String destination;
    @SerializedName("category")
    private String category;
    @SerializedName("duration")
    private String duration;
    @SerializedName("price")
    private double price;
    @SerializedName("available_spots")
    private int availableSpots;
    @SerializedName("image_url")
    private String imageUrl;

    public ActivityItem(int id, String name, String destination, String category, String duration, double price, int availableSpots, String imageUrl) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.category = category;
        this.duration = duration;
        this.price = price;
        this.availableSpots = availableSpots;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDestination() { return destination; }
    public String getCategory() { return category; }
    public String getDuration() { return duration; }
    public double getPrice() { return price; }
    public int getAvailableSpots() { return availableSpots; }
    public String getImageUrl() { return imageUrl; }
}
