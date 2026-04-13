package com.example.xplorenow_android;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CatalogApi {
    @GET("api/catalogs/travel-interests")
    Call<InterestResponse> getTravelInterests();
}
