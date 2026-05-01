package com.example.xplorenow_android.data.model;

public class News {

    private int id;
    private String title;
    private String description;
    private int imageRes;

    public News(int id, String title, String description, int imageRes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageRes = imageRes;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageRes() {
        return imageRes;
    }
}