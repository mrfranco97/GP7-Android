package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.NotificationItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface NotificationsApi {
    @GET("api/notifications/poll")
    Call<NotificationsResponse> pollNotifications();

    @GET("api/notifications")
    Call<NotificationsResponse> getHistory();

    @PATCH("api/notifications/{id}/read")
    Call<NotificationItem> markAsRead(@Path("id") String id);
}
