package com.example.xplorenow_android.ui.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.model.News;
import com.example.xplorenow_android.data.network.NewsApi;
import com.example.xplorenow_android.data.network.NewsResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";

    @Inject
    NewsApi newsApi;

    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news, container, false);

        progressBar = view.findViewById(R.id.progressNews);
        Button btnBack = view.findViewById(R.id.btnBackNews);
        // En un Fragment, el botón atrás suele navegar hacia atrás en el NavController
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        RecyclerView recyclerNews = view.findViewById(R.id.recyclerNews);
        List<News> newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsList);

        recyclerNews.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerNews.setAdapter(newsAdapter);

        loadNews();
        return view;
    }

    private void loadNews() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        newsApi.getNews(10).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        newsAdapter.setItems(response.body().getItems());
                    } else {
                        Toast.makeText(getContext(), "No se pudieron cargar las noticias", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching news", t);
                    Toast.makeText(getContext(), "Error de conexion al cargar noticias", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
