package com.example.xplorenow_android.ui.experience;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.data.network.ExperienceApi;
import com.example.xplorenow_android.data.network.RetrofitClient;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExperienceViewModel extends ViewModel {
    private final MutableLiveData<String> categoryLiveData = new MutableLiveData<>("All");
    private final LiveData<PagingData<Experience>> pagingDataLiveData;
    private final MutableLiveData<List<Experience>> recommendedLiveData = new MutableLiveData<>();
    private final ExperienceApi api;

    public ExperienceViewModel() {
        this.api = RetrofitClient.getExperienceApi();
        
        LiveData<PagingData<Experience>> source = Transformations.switchMap(categoryLiveData, category -> {
            Pager<Integer, Experience> pager = new Pager<>(
                    new PagingConfig(10, 5, false),
                    () -> new ExperiencePagingSource(api, category)
            );
            return PagingLiveData.getLiveData(pager);
        });

        pagingDataLiveData = PagingLiveData.cachedIn(source, ViewModelKt.getViewModelScope(this));

        // Disparamos la carga inicial de recomendaciones
        fetchRecommendations();
    }

    public void fetchRecommendations() {
        api.getRecommendedExperiences().enqueue(new Callback<ExperienceResponse>() {
            @Override
            public void onResponse(Call<ExperienceResponse> call, Response<ExperienceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendedLiveData.setValue(response.body().getItems());
                }
            }

            @Override
            public void onFailure(Call<ExperienceResponse> call, Throwable t) {
            }
        });
    }

    public LiveData<PagingData<Experience>> getPagingDataLiveData() {
        return pagingDataLiveData;
    }

    public LiveData<List<Experience>> getRecommendedLiveData() {
        return recommendedLiveData;
    }

    public void setCategory(String category) {
        if (!category.equals(categoryLiveData.getValue())) {
            categoryLiveData.setValue(category);
        }
    }
}
