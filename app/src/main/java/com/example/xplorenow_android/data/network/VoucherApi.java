package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.Voucher;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VoucherApi {

    @GET("api/bookings/{id}/voucher")
    Call<Voucher> getVoucher(@Path("id") String bookingId);
}
