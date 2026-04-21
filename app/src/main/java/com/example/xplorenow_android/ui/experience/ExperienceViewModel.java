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
import com.example.xplorenow_android.data.network.CatalogApi;
import com.example.xplorenow_android.data.network.CategoryResponse;
import com.example.xplorenow_android.data.network.ExperienceApi;
import com.example.xplorenow_android.data.network.ExperienceResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ExperienceViewModel extends ViewModel {
    private final MutableLiveData<ExperienceFilters> filtersLiveData = new MutableLiveData<>(new ExperienceFilters());
    private final LiveData<PagingData<Experience>> pagingDataLiveData;
    private final MutableLiveData<List<Experience>> recommendedLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<com.example.xplorenow_android.data.network.Category>> categoriesCatalogLiveData = new MutableLiveData<>();
    private final MutableLiveData<Experience> experienceDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> detailLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> detailErrorLiveData = new MutableLiveData<>();
    
    private final ExperienceApi api;
    private final CatalogApi catalogApi;

    @Inject
    public ExperienceViewModel(ExperienceApi api, CatalogApi catalogApi) {
        this.api = api;
        this.catalogApi = catalogApi;
        
        LiveData<PagingData<Experience>> source = Transformations.switchMap(filtersLiveData, filters -> {
            Pager<Integer, Experience> pager = new Pager<>(
                    new PagingConfig(10, 5, false),
                    () -> new ExperiencePagingSource(api, filters)
            );
            return PagingLiveData.getLiveData(pager);
        });

        pagingDataLiveData = PagingLiveData.cachedIn(source, ViewModelKt.getViewModelScope(this));

        fetchRecommendations();
        fetchCategoriesCatalog();
    }

    private void fetchCategoriesCatalog() {
        catalogApi.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesCatalogLiveData.setValue(response.body().getItems());
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
            }
        });
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

    public void fetchExperienceDetail(int id) {
        detailLoadingLiveData.setValue(true);
        api.getExperienceDetail(String.valueOf(id)).enqueue(new Callback<Experience>() {
            @Override
            public void onResponse(Call<Experience> call, Response<Experience> response) {
                detailLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    experienceDetailLiveData.setValue(response.body());
                } else {
                    detailErrorLiveData.setValue("Error al cargar el detalle");
                }
            }

            @Override
            public void onFailure(Call<Experience> call, Throwable t) {
                detailLoadingLiveData.setValue(false);
                detailErrorLiveData.setValue(t.getMessage());
            }
        });
    }

    public LiveData<PagingData<Experience>> getPagingDataLiveData() {
        return pagingDataLiveData;
    }

    public LiveData<List<Experience>> getRecommendedLiveData() {
        return recommendedLiveData;
    }

    public LiveData<List<com.example.xplorenow_android.data.network.Category>> getCategoriesCatalogLiveData() {
        return categoriesCatalogLiveData;
    }

    public LiveData<Experience> getExperienceDetailLiveData() {
        return experienceDetailLiveData;
    }

    public LiveData<Boolean> getDetailLoadingLiveData() {
        return detailLoadingLiveData;
    }

    public LiveData<String> getDetailErrorLiveData() {
        return detailErrorLiveData;
    }

    public void setCategory(String category) {
        ExperienceFilters current = filtersLiveData.getValue();
        if (current != null) {
            ExperienceFilters next = new ExperienceFilters();
            next.setDestination(current.getDestination());
            next.setDate(current.getDate());
            next.setMinPrice(current.getMinPrice());
            next.setMaxPrice(current.getMaxPrice());
            next.setCategory(category);
            filtersLiveData.setValue(next);
        }
    }

    public void applyFilters(String destination, String category, String date, Integer minPrice, Integer maxPrice) {
        ExperienceFilters next = new ExperienceFilters();
        next.setCategory(category);
        next.setDestination(destination);
        next.setDate(date);
        next.setMinPrice(minPrice);
        next.setMaxPrice(maxPrice);
        filtersLiveData.setValue(next);
    }
    
    public ExperienceFilters getFilters() {
        return filtersLiveData.getValue();
    }
}
