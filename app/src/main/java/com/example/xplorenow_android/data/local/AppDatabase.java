package com.example.xplorenow_android.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.xplorenow_android.data.model.Booking;

@Database(entities = {Booking.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookingDao bookingDao();
}
