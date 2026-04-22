package com.example.xplorenow_android.data.model;

import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("activityStars")
    private int activityStars;
    
    @SerializedName("guideStars")
    private int guideStars;
    
    @SerializedName("comment")
    private String comment;

    public Rating() {}

    public Rating(int activityStars, int guideStars, String comment) {
        this.activityStars = activityStars;
        this.guideStars = guideStars;
        this.comment = comment;
    }

    public int getActivityStars() { return activityStars; }
    public int getGuideStars() { return guideStars; }
    public String getComment() { return comment; }
}
