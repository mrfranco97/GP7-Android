package com.example.xplorenow_android.data.local;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public class NetworkStatusManager {

    private static final Preferences.Key<Boolean> IS_ONLINE = PreferencesKeys.booleanKey("is_online");
    private final RxDataStore<Preferences> dataStore;

    @Inject
    public NetworkStatusManager(@ApplicationContext Context context) {
        this.dataStore = new RxPreferenceDataStoreBuilder(context, "network_settings").build();
    }

    public Flowable<Boolean> isOnline() {
        return dataStore.data().map(prefs -> {
            Boolean online = prefs.get(IS_ONLINE);
            return online == null || online;
        });
    }

    public void setOnline(boolean online) {
        dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(IS_ONLINE, online);
            return Single.just(mutablePreferences);
        });
    }
}
