package com.example.xplorenow_android.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.xplorenow_android.data.model.Booking;
import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class BookingDao {
    @Query("SELECT * FROM bookings ORDER BY date DESC")
    public abstract List<Booking> getAllBookings();

    @Query("SELECT * FROM bookings WHERE id = :id")
    public abstract Booking getBookingById(String id);

    @Query("SELECT * FROM bookings WHERE isPendingCancellation = 1")
    public abstract List<Booking> getPendingCancellations();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertBooking(Booking booking);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertBookingsInternal(List<Booking> bookings);

    @Query("DELETE FROM bookings WHERE id NOT IN (:ids) AND isPendingCancellation = 0")
    public abstract void deleteExcept(List<String> ids);

    @Transaction
    public void syncBookings(List<Booking> newBookings) {
        List<String> ids = new ArrayList<>();
        for (Booking b : newBookings) {
            ids.add(b.getId());
            Booking existing = getBookingById(b.getId());
            if (existing != null) {
                if (b.getActivity() == null) {
                    b.setActivity(existing.getActivity());
                }
                // Preserve pending state if server hasn't processed it yet
                if (existing.isPendingCancellation() && "confirmada".equalsIgnoreCase(b.getStatus())) {
                    b.setPendingCancellation(true);
                    b.setStatus("cancelada");
                }
            }
        }
        insertBookingsInternal(newBookings);
        if (!ids.isEmpty()) {
            deleteExcept(ids);
        } else {
            deleteAll();
        }
    }

    @Query("DELETE FROM bookings WHERE isPendingCancellation = 0")
    public abstract void deleteAll();
}
