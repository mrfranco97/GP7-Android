package com.example.xplorenow_android.data.local;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenManager {

    private static final String KEY_TOKEN = "jwt_token";

    private final SharedPreferences prefs;

    @Inject
    public TokenManager(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean hasToken() {
        return getToken() != null;
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }
}
