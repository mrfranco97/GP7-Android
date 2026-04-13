package com.example.xplorenow_android;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Interest>> interestsCatalogLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> updateSuccessLiveData = new MutableLiveData<>();
    private final AuthApi authApi;
    private final CatalogApi catalogApi;

    public ProfileViewModel() {
        this.authApi = RetrofitClient.getAuthApi();
        this.catalogApi = RetrofitClient.getCatalogApi();
    }

    public LiveData<User> getUserLiveData() { return userLiveData; }
    public LiveData<List<Interest>> getInterestsCatalogLiveData() { return interestsCatalogLiveData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getLoadingLiveData() { return loadingLiveData; }
    public LiveData<Boolean> getUpdateSuccessLiveData() { return updateSuccessLiveData; }

    public void fetchProfile() {
        loadingLiveData.setValue(true);
        authApi.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    userLiveData.setValue(response.body());
                    fetchInterestsCatalog();
                } else {
                    loadingLiveData.setValue(false);
                    errorLiveData.setValue("Error al cargar perfil");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

    private void fetchInterestsCatalog() {
        catalogApi.getTravelInterests().enqueue(new Callback<InterestResponse>() {
            @Override
            public void onResponse(Call<InterestResponse> call, Response<InterestResponse> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    interestsCatalogLiveData.setValue(response.body().getItems());
                }
            }

            @Override
            public void onFailure(Call<InterestResponse> call, Throwable t) {
                loadingLiveData.setValue(false);
            }
        });
    }

    public void updateProfile(String name, String email, String phone, List<String> selectedInterests) {
        loadingLiveData.setValue(true);
        User updatedUser = new User(name, email, phone);
        updatedUser.setPreferences(new UserPreferences(selectedInterests));

        authApi.updateUserProfile(updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loadingLiveData.setValue(false);
                if (response.isSuccessful()) {
                    userLiveData.setValue(response.body());
                    updateSuccessLiveData.setValue(true);
                } else {
                    errorLiveData.setValue("Error al actualizar perfil");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loadingLiveData.setValue(false);
                errorLiveData.setValue(t.getMessage());
            }
        });
    }

    public void resetUpdateSuccess() {
        updateSuccessLiveData.setValue(null);
    }
}
