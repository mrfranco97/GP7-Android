package com.example.xplorenow_android;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface AuthApi {
    @GET("api/auth/me")
    Call<User> getUserProfile();

    @PUT("api/auth/me")
    Call<User> updateUserProfile(@Body User user);
}
