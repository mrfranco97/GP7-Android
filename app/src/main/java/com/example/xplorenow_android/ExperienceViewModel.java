package com.example.xplorenow_android;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

public class ExperienceViewModel extends ViewModel {
    private final MutableLiveData<String> categoryLiveData = new MutableLiveData<>("All");
    private final LiveData<PagingData<ActivityItem>> pagingDataLiveData;

    public ExperienceViewModel() {
        ExperienceApi api = RetrofitClient.getExperienceApi();
        
        pagingDataLiveData = Transformations.switchMap(categoryLiveData, category -> {
            Pager<Integer, ActivityItem> pager = new Pager<>(
                    new PagingConfig(10, 5, false),
                    () -> new ActivityPagingSource(api, category)
            );
            return PagingLiveData.getLiveData(pager);
        });
    }

    public LiveData<PagingData<ActivityItem>> getPagingDataLiveData() {
        return pagingDataLiveData;
    }

    public void setCategory(String category) {
        if (!category.equals(categoryLiveData.getValue())) {
            categoryLiveData.setValue(category);
        }
    }
}
