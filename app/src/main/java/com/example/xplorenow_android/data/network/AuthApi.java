package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.User;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface AuthApi {

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("api/auth/otp/request")
    Call<OtpResponse> requestOtp(@Body OtpRequest request);

    @POST("api/auth/otp/resend")
    Call<OtpResponse> resendOtp(@Body OtpRequest request);

    @POST("api/auth/otp/verify")
    Call<AuthResponse> verifyOtp(@Body VerifyOtpRequest request);

    @GET("api/auth/me")
    Call<User> getUserProfile();

    @PUT("api/auth/me")
    Call<User> updateUserProfile(@Body User user);

    @Multipart
    @POST("api/auth/me/profile-picture")
    Call<ResponseBody> uploadProfilePicture(@Part MultipartBody.Part profilePicture);

    @GET("api/auth/me/profile-picture")
    Call<ResponseBody> getProfilePicture();
}
