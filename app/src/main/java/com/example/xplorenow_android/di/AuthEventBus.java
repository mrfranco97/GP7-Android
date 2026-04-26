package com.example.xplorenow_android.di;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthEventBus {

    private final MutableLiveData<Boolean> sessionExpired = new MutableLiveData<>();

    @Inject
    public AuthEventBus() {}

    public LiveData<Boolean> getSessionExpired() {
        return sessionExpired;
    }

    public void emitSessionExpired() {
        // postValue es thread-safe: puede llamarse desde el hilo de red del interceptor
        sessionExpired.postValue(true);
    }
}
