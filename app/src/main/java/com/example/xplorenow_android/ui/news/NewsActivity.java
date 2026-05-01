package com.example.xplorenow_android.ui.news;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.model.News;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Botón volver
        Button btnBack = findViewById(R.id.btnBackNews);
        btnBack.setOnClickListener(v -> finish());

        // RecyclerView
        RecyclerView recyclerNews = findViewById(R.id.recyclerNews);

        List<News> newsList = new ArrayList<>();

        newsList.add(new News(
                1,
                "50% OFF en escapadas de fin de semana",
                "Promoción especial para viajeros que reserven experiencias seleccionadas.",
                R.drawable.news_promo
        ));

        newsList.add(new News(
                2,
                "Nuevo destino: Mendoza aventura",
                "Nuevas actividades de montaña, vino y turismo gastronómico.",
                R.drawable.news_mendoza
        ));

        newsList.add(new News(
                3,
                "Promo especial en Buenos Aires",
                "Descuentos en visitas guiadas, experiencias urbanas y recorridos culturales.",
                R.drawable.news_bsas
        ));

        recyclerNews.setLayoutManager(new LinearLayoutManager(this));
        recyclerNews.setAdapter(new NewsAdapter(newsList));
    }
}