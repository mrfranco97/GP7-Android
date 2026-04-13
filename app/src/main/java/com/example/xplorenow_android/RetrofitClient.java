package com.example.xplorenow_android;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private static Retrofit retrofit = null;

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ExperienceApi getExperienceApi() {
        return getRetrofitInstance().create(ExperienceApi.class);
    }

    public static AuthApi getAuthApi() {
        return getRetrofitInstance().create(AuthApi.class);
    }

    public static CatalogApi getCatalogApi() {
        return getRetrofitInstance().create(CatalogApi.class);
    }
}
