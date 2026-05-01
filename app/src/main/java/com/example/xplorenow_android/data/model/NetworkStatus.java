package com.example.xplorenow_android.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "network_status")
public class NetworkStatus {
    @PrimaryKey
    public int id = 0;
    public boolean isOnline;

    public NetworkStatus(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
