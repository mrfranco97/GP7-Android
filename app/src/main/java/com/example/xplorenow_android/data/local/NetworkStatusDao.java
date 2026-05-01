package com.example.xplorenow_android.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.xplorenow_android.data.model.NetworkStatus;

@Dao
public interface NetworkStatusDao {
    @Query("SELECT isOnline FROM network_status WHERE id = 0")
    LiveData<Boolean> isOnline();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateStatus(NetworkStatus status);
}
