package com.example.xplorenow_android;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExperiencePagingSource extends ListenableFuturePagingSource<Integer, Experience> {

    private static final String TAG = "ExperiencePagingSource";
    private final ExperienceApi api;
    private final String category;

    public ExperiencePagingSource(ExperienceApi api, String category) {
        this.api = api;
        this.category = category;
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

        Log.d(TAG, "Loading page: " + page + " with limit: " + limit + " category: " + category);

        SettableFuture<LoadResult<Integer, Experience>> future = SettableFuture.create();

        String categoryParam = (category == null || "All".equals(category)) ? null : category.toLowerCase();

        api.getExperiences(page, limit, categoryParam).enqueue(new Callback<ExperienceResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExperienceResponse> call, @NonNull Response<ExperienceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Experience> items = response.body().getItems();
                    Log.d(TAG, "Successfully loaded " + items.size() + " items");
                    future.set(new LoadResult.Page<>(
                            items,
                            page == 1 ? null : page - 1,
                            items.isEmpty() ? null : page + 1
                    ));
                } else {
                    String errorMsg = "API call failed with code: " + response.code();
                    Log.e(TAG, errorMsg);
                    future.set(new LoadResult.Error<>(new Exception(errorMsg)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExperienceResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error loading paging data", t);
                future.set(new LoadResult.Error<>(t));
            }
        });

        return future;
    }
}
