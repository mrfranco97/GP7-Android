package com.example.xplorenow_android.di;

import android.content.Context;
import androidx.room.Room;
import com.example.xplorenow_android.data.local.AppDatabase;
import com.example.xplorenow_android.data.local.BookingDao;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "xplorenow_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    public BookingDao provideBookingDao(AppDatabase database) {
        return database.bookingDao();
    }
}
