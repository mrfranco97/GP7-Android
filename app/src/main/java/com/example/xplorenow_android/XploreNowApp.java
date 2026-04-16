package com.example.xplorenow_android;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;


@HiltAndroidApp
public class XploreNowApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Aquí puedes inicializar otras librerías si fuera necesario
    }
}
