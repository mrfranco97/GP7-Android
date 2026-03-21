package com.example.xplorenow_android;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ExperienceApi {
    @GET("api/experiences")
    Call<ExperienceResponse> getExperiences(
            @Query("page") int page,
            @Query("limit") int limit
    );
}
