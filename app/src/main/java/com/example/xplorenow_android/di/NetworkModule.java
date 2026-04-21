package com.example.xplorenow_android.di;

import com.example.xplorenow_android.data.network.AuthApi;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.CatalogApi;
import com.example.xplorenow_android.data.network.ExperienceApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    private static final String BASE_URL = "http://10.0.2.2:3000/";

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public ExperienceApi provideExperienceApi(Retrofit retrofit) {
        return retrofit.create(ExperienceApi.class);
    }

    @Provides
    @Singleton
    public AuthApi provideAuthApi(Retrofit retrofit) {
        return retrofit.create(AuthApi.class);
    }

    @Provides
    @Singleton
    public CatalogApi provideCatalogApi(Retrofit retrofit) {
        return retrofit.create(CatalogApi.class);
    }

    @Provides
    @Singleton
    public BookingApi provideBookingApi(Retrofit retrofit) {
        return retrofit.create(BookingApi.class);
    }
}
