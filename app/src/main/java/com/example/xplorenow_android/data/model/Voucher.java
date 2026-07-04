package com.example.xplorenow_android.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import androidx.annotation.NonNull;

@Entity(tableName = "vouchers")
public class Voucher {

    @PrimaryKey
    @NonNull
    @SerializedName("booking_id")

    private String bookingId;

    @SerializedName("activity_name")
    private String activityName;

    @SerializedName("date")
    private String date;

    @SerializedName("time_slot")
    private String timeSlot;

    @SerializedName("meeting_point")
    private String meetingPoint;

    @SerializedName("guide_name")
    private String guideName;

    @SerializedName("participants")
    private int participants;

    @SerializedName("status")
    private String status;

    @SerializedName("check_in_status")
    private String checkInStatus;

    @SerializedName("checked_in_at")
    private String checkedInAt;

    // Getters and Setters

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getMeetingPoint() { return meetingPoint; }
    public void setMeetingPoint(String meetingPoint) { this.meetingPoint = meetingPoint; }

    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public int getParticipants() { return participants; }
    public void setParticipants(int participants) { this.participants = participants; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCheckInStatus() { return checkInStatus; }
    public void setCheckInStatus(String checkInStatus) { this.checkInStatus = checkInStatus; }

    public String getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(String checkedInAt) { this.checkedInAt = checkedInAt; }
}
