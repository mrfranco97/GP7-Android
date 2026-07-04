package com.example.xplorenow_android.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.data.model.NetworkStatus;
import com.example.xplorenow_android.data.model.Voucher;

@Database(entities = {Booking.class, NetworkStatus.class, Voucher.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookingDao bookingDao();
    public abstract NetworkStatusDao networkStatusDao();
    public abstract VoucherDao voucherDao();
}
