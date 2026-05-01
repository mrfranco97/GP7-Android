package com.example.xplorenow_android.ui.experience;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.xplorenow_android.data.model.Favorite;
import com.example.xplorenow_android.data.network.FavoriteApi;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class SharedFavoriteViewModel extends ViewModel {

    public static class FavoriteUpdate {
        private final int experienceId;
        private final boolean isFavorite;

        public FavoriteUpdate(int experienceId, boolean isFavorite) {
            this.experienceId = experienceId;
            this.isFavorite = isFavorite;
        }

        public int getExperienceId() { return experienceId; }
        public boolean isFavorite() { return isFavorite; }
    }

    private final FavoriteApi favoriteApi;
    private final Context context;

    private final MutableLiveData<FavoriteUpdate> favoriteUpdate = new MutableLiveData<>();

    @Inject
    public SharedFavoriteViewModel(FavoriteApi favoriteApi, @ApplicationContext Context context) {
        this.favoriteApi = favoriteApi;
        this.context = context;
    }

    public LiveData<FavoriteUpdate> getFavoriteUpdate() {
        return favoriteUpdate;
    }

    public void toggleFavorite(int experienceId, boolean isCurrentlyFavorite) {
        if (isCurrentlyFavorite) {
            favoriteApi.removeFavorite(experienceId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        favoriteUpdate.postValue(new FavoriteUpdate(experienceId, false));
                        showToast("Eliminado de favoritos");
                    } else {
                        showToast("Error al eliminar de favoritos");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    showToast("Error de conexión");
                }
            });
        } else {
            favoriteApi.addFavorite(experienceId).enqueue(new Callback<Favorite>() {
                @Override
                public void onResponse(@NonNull Call<Favorite> call, @NonNull Response<Favorite> response) {
                    if (response.isSuccessful()) {
                        favoriteUpdate.postValue(new FavoriteUpdate(experienceId, true));
                        showToast("Añadido a favoritos");
                    } else {
                        showToast("Error al añadir a favoritos");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Favorite> call, @NonNull Throwable t) {
                    showToast("Error de conexión");
                }
            });
        }
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() -> 
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }
}
