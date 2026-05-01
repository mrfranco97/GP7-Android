package com.example.xplorenow_android.ui.experience;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.data.network.ExperienceApi;
import com.example.xplorenow_android.data.network.ExperienceResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExperiencePagingSource extends ListenableFuturePagingSource<Integer, Experience> {

    private static final String TAG = "ExperiencePagingSource";
    private final ExperienceApi api;
    private final ExperienceFilters filters;

    public ExperiencePagingSource(ExperienceApi api, ExperienceFilters filters) {
        this.api = api;
        this.filters = filters;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Experience> pagingState) {
        return pagingState.getAnchorPosition();
    }

    @NonNull
    @Override
    public ListenableFuture<LoadResult<Integer, Experience>> loadFuture(@NonNull LoadParams<Integer> loadParams) {
        Integer key = loadParams.getKey();
        int page = key != null ? key : 1;
        int limit = loadParams.getLoadSize();

        Log.d(TAG, "Loading page: " + page + " with limit: " + limit + " filters: " + filters);

        SettableFuture<LoadResult<Integer, Experience>> future = SettableFuture.create();

        String categoryParam = (filters.getCategory() == null || "All".equals(filters.getCategory())) 
                ? null : filters.getCategory();

        api.getExperiences(
                page,
                limit,
                filters.getDestination(),
                categoryParam,
                filters.getDate(),
                filters.getMinPrice(),
                filters.getMaxPrice(),
                filters.getLocation(),
                filters.getAvailable()
        ).enqueue(new Callback<ExperienceResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExperienceResponse> call, @NonNull Response<ExperienceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExperienceResponse body = response.body();
                    List<Experience> rawItems = body.getItems();
                    
                    List<Experience> items = new ArrayList<>();
                    Set<String> seen = new HashSet<>();
                    
                    if (rawItems != null) {
                        for (Experience item : rawItems) {
                            if (item == null || item.getName() == null) continue;
                            
                            String uniqueKey = (item.getName().trim() + "|" +
                                         (item.getDestination() != null ? item.getDestination().trim() : ""))
                                         .toLowerCase();
                            
                            if (seen.add(uniqueKey)) {
                                items.add(item);
                            }
                        }
                    }

                    Log.d(TAG, "Loaded " + items.size() + " unique items from page " + page);
                    
                    Integer nextKey = null;
                    if (body.getPagination() != null) {
                        if (body.getPagination().hasNextPage()) {
                            nextKey = page + 1;
                        }
                    } else {
                        nextKey = (rawItems == null || rawItems.isEmpty()) ? null : page + 1;
                    }

                    future.set(new LoadResult.Page<>(
                            items,
                            page == 1 ? null : page - 1,
                            nextKey
                    ));
                } else {
                    Log.e(TAG, "API Error: " + response.code());
                    future.set(new LoadResult.Error<>(new Exception("API call failed")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExperienceResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network Error", t);
                future.set(new LoadResult.Error<>(t));
            }
        });

        return future;
    }
}
