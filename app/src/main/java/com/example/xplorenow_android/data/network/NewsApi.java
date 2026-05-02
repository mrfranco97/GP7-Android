package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("api/news")
    Call<NewsResponse> getNews(@Query("limit") int limit);

    @GET("api/news/{id}")
    Call<News> getNewsDetail(@Path("id") String id);
}
