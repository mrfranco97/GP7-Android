package com.example.xplorenow_android;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

public class ExperienceViewModel extends ViewModel {
    private final MutableLiveData<String> categoryLiveData = new MutableLiveData<>("All");
    private final LiveData<PagingData<Experience>> pagingDataLiveData;
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
    }

    public LiveData<PagingData<Experience>> getPagingDataLiveData() {
        return pagingDataLiveData;
    }

    public void setCategory(String category) {
        if (!category.equals(categoryLiveData.getValue())) {
            categoryLiveData.setValue(category);
        }
    }
}
