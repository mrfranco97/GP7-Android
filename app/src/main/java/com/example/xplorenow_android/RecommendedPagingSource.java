package com.example.xplorenow_android;

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

public class RecommendedPagingSource extends ListenableFuturePagingSource<Integer, Experience> {

    private final ExperienceApi api;

    public RecommendedPagingSource(ExperienceApi api) {
        this.api = api;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Experience> pagingState) {
        return pagingState.getAnchorPosition();
    }

    @NonNull
    @Override
    public ListenableFuture<LoadResult<Integer, Experience>> loadFuture(@NonNull LoadParams<Integer> loadParams) {
        SettableFuture<LoadResult<Integer, Experience>> future = SettableFuture.create();

        api.getRecommendedExperiences().enqueue(new Callback<ExperienceResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExperienceResponse> call, @NonNull Response<ExperienceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Experience> items = response.body().getItems();
                    future.set(new LoadResult.Page<>(
                            items,
                            null,
                            null
                    ));
                } else {
                    future.set(new LoadResult.Error<>(new Exception("Error loading recommendations")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExperienceResponse> call, @NonNull Throwable t) {
                future.set(new LoadResult.Error<>(t));
            }
        });

        return future;
    }
}
