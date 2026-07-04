package com.example.xplorenow_android.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.xplorenow_android.data.model.Voucher;

@Dao
public interface VoucherDao {

    @Query("SELECT * FROM vouchers WHERE bookingId = :bookingId")
    Voucher getVoucherByBookingId(String bookingId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVoucher(Voucher voucher);

    @Query("DELETE FROM vouchers WHERE bookingId = :bookingId")
    void deleteVoucher(String bookingId);
}
