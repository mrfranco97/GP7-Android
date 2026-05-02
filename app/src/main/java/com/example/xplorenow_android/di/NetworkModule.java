package com.example.xplorenow_android.di;

import android.util.Log;

import com.example.xplorenow_android.data.local.AppDatabase;
import com.example.xplorenow_android.data.local.TokenManager;
import com.example.xplorenow_android.data.model.NetworkStatus;
import com.example.xplorenow_android.data.network.AuthApi;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.CatalogApi;
import com.example.xplorenow_android.data.network.ExperienceApi;
import com.example.xplorenow_android.data.network.NewsApi;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private static final String TAG = "NetworkModule";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttp(TokenManager tokenManager, AuthEventBus authEventBus, AppDatabase db) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String token = tokenManager.getToken();
                    okhttp3.Request request = chain.request();
                    if (token != null) {
                        request = request.newBuilder()
                                .addHeader("Authorization", "Bearer " + token)
                                .build();
                    }

                    try {
                        okhttp3.Response response = chain.proceed(request);
                        
                        db.networkStatusDao().updateStatus(new NetworkStatus(true));

                        if (response.code() == 401) {
                            Log.d(TAG, "Token vencido — emitiendo sesión expirada");
                            tokenManager.clearToken();
                            tokenManager.setBiometricEnabled(false);
                            authEventBus.emitSessionExpired();
                        }

                        return response;
                    } catch (IOException e) {
                        Log.e(TAG, "Error de red detectado: " + e.getMessage());
                        db.networkStatusDao().updateStatus(new NetworkStatus(false));
                        throw e;
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
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

    @Provides
    @Singleton
    public NewsApi provideNewsApi(Retrofit retrofit) {
        return retrofit.create(NewsApi.class);
    }
}
