package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.Favorite;
import com.example.xplorenow_android.data.network.FavoriteResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
public interface FavoriteApi {
    @GET("api/favorites")
    Call<FavoriteResponse> getFavorites();


    @POST("api/favorites/{experienceId}")
    Call<Favorite> addFavorite(@Path("experienceId") int experienceId);

    @PATCH("api/favorites/{experienceId}/seen")
    Call<Favorite> markFavoriteSeen(@Path("experienceId") int experienceId);

    @DELETE("api/favorites/{experienceId}")
    Call<Void> removeFavorite(@Path("experienceId") int experienceId);
}
