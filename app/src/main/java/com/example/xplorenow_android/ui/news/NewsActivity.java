package com.example.xplorenow_android.ui.news;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
public class NewsActivity extends AppCompatActivity {

    private static final String TAG = "NewsActivity";

    @Inject
    NewsApi newsApi;

    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Button btnBack = findViewById(R.id.btnBackNews);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView recyclerNews = findViewById(R.id.recyclerNews);
        List<News> newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsList);

        recyclerNews.setLayoutManager(new LinearLayoutManager(this));
        recyclerNews.setAdapter(newsAdapter);

        loadNews();
    }

    private void loadNews() {
        newsApi.getNews(10).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newsAdapter.setItems(response.body().getItems());
                } else {
                    Toast.makeText(NewsActivity.this, "No se pudieron cargar las noticias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching news", t);
                Toast.makeText(NewsActivity.this, "Error de conexion al cargar noticias", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
