package com.example.xplorenow_android.data.local;

import androidx.room.TypeConverter;
import com.example.xplorenow_android.data.model.Booking;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromStringList(List<String> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<String> toStringList(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromExperienceSummary(Booking.ExperienceSummary experience) {
        return gson.toJson(experience);
    }

    @TypeConverter
    public static Booking.ExperienceSummary toExperienceSummary(String value) {
        return gson.fromJson(value, Booking.ExperienceSummary.class);
    }

    @TypeConverter
    public static String fromActivityDetail(Booking.ActivityDetail activity) {
        return gson.toJson(activity);
    }

    @TypeConverter
    public static Booking.ActivityDetail toActivityDetail(String value) {
        return gson.fromJson(value, Booking.ActivityDetail.class);
    }

    @TypeConverter
    public static String fromRatingSummary(Booking.RatingSummary rating) {
        return gson.toJson(rating);
    }

    @TypeConverter
    public static Booking.RatingSummary toRatingSummary(String value) {
        return gson.fromJson(value, Booking.RatingSummary.class);
    }
}
