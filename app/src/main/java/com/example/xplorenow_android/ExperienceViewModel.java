package com.example.xplorenow_android;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

public class ExperienceViewModel extends ViewModel {
    private final LiveData<PagingData<ActivityItem>> pagingDataLiveData;

    public ExperienceViewModel() {
        ExperienceApi api = RetrofitClient.getExperienceApi();
        Pager<Integer, ActivityItem> pager = new Pager<>(
                new PagingConfig(10, 5, false),
                () -> new ActivityPagingSource(api)
        );
        pagingDataLiveData = PagingLiveData.getLiveData(pager);
    }

    public LiveData<PagingData<ActivityItem>> getPagingDataLiveData() {
        return pagingDataLiveData;
    }
}
