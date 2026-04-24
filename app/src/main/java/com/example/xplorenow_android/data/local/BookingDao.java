package com.example.xplorenow_android.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.xplorenow_android.data.model.Booking;
import java.util.List;

@Dao
public interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY date DESC")
    List<Booking> getAllBookings();

    @Query("SELECT * FROM bookings WHERE id = :id")
    Booking getBookingById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookings(List<Booking> bookings);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBooking(Booking booking);

    @Query("DELETE FROM bookings")
    void deleteAll();
}
