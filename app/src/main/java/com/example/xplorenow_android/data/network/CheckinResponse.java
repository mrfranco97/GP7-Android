package com.example.xplorenow_android.data.network;

import com.google.gson.annotations.SerializedName;

public class CheckinResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private String message;

    @SerializedName("activity_name")
    private String activityName;

    @SerializedName("already_checked_in")
    private Boolean alreadyCheckedIn;

    public boolean isSuccess() { return success; }
    public String getResult() { return result; }
    public String getMessage() { return message; }
    public String getActivityName() { return activityName; }
    public Boolean getAlreadyCheckedIn() { return alreadyCheckedIn; }
}
