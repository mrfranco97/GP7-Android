package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.Experience;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExperienceApi {
    @GET("api/experiences")
    Call<ExperienceResponse> getExperiences(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("destination") String destination,
            @Query("category") String category,
            @Query("date") String date,
            @Query("minPrice") Integer minPrice,
            @Query("maxPrice") Integer maxPrice
    );

    @GET("api/experiences/recommended")
    Call<ExperienceResponse> getRecommendedExperiences();

    @GET("api/experiences/{id}")
    Call<Experience> getExperienceDetail(@Path("id") String id);
}
