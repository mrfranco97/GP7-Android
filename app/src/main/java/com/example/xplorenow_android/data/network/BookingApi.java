package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.BookingRequest;
import com.example.xplorenow_android.data.model.Rating;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingApi {
    @GET("api/experiences/{id}/availability")
    Call<AvailabilityResponse> getAvailability(@Path("id") String id, @Query("date") String date);

    @POST("api/bookings")
    Call<BookingResponse> createBooking(@Body BookingRequest request);

    @GET("api/bookings/me")
    Call<MyBookingsResponse> getMyBookings();

    @DELETE("api/bookings/{id}")
    Call<BookingCancellationResponse> cancelBooking(@Path("id") String id);

    @POST("api/bookings/{bookingId}/rating")
    Call<BookingResponse> submitRating(@Path("bookingId") String bookingId, @Body Rating rating);
}
