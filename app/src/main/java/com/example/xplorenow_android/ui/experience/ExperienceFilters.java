package com.example.xplorenow_android.ui.experience;

import androidx.annotation.NonNull;

public class ExperienceFilters {
    private String destination;
    private String category;
    private String date;
    private Integer minPrice;
    private Integer maxPrice;
    private String location;
    private Boolean available;

    public ExperienceFilters() {
        this.destination = null;
        this.category = "All";
        this.date = null;
        this.minPrice = null;
        this.maxPrice = null;
        this.location = null;
        this.available = null;
    }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Integer getMinPrice() { return minPrice; }
    public void setMinPrice(Integer minPrice) { this.minPrice = minPrice; }

    public Integer getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Integer maxPrice) { this.maxPrice = maxPrice; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    @NonNull
    @Override
    public String toString() {
        return "Filters{" +
                "dest='" + destination + '\'' +
                ", cat='" + category + '\'' +
                ", date='" + date + '\'' +
                ", min=" + minPrice +
                ", max=" + maxPrice +
                ", loc='" + location + '\'' +
                ", avail=" + available +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperienceFilters that = (ExperienceFilters) o;
        return java.util.Objects.equals(destination, that.destination) &&
                java.util.Objects.equals(category, that.category) &&
                java.util.Objects.equals(date, that.date) &&
                java.util.Objects.equals(minPrice, that.minPrice) &&
                java.util.Objects.equals(maxPrice, that.maxPrice) &&
                java.util.Objects.equals(location, that.location) &&
                java.util.Objects.equals(available, that.available);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(destination, category, date, minPrice, maxPrice, location, available);
    }
}
