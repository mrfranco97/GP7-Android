package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.ui.profile.InterestResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CatalogApi {
    @GET("api/catalogs/travel-interests")
    Call<InterestResponse> getTravelInterests();
}
