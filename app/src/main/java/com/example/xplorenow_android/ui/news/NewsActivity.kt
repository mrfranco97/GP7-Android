package com.example.xplorenow_android.ui.news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xplorenow_android.R
import com.example.xplorenow_android.data.model.News
import android.widget.Button
class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        findViewById<Button>(R.id.btnBackNews).setOnClickListener {
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerNews = findViewById<RecyclerView>(R.id.recyclerNews)

        val newsList = listOf(
            News(
                1,
                "50% OFF en escapadas de fin de semana",
                "Promoción especial para viajeros que reserven experiencias seleccionadas.",
                ""
            ),
            News(
                2,
                "Nuevo destino: Mendoza aventura",
                "Nuevas actividades de montaña, vino y turismo gastronómico.",
                ""
            ),
            News(
                3,
                "Promo especial en Buenos Aires",
                "Descuentos en visitas guiadas, experiencias urbanas y recorridos culturales.",
                ""
            )
        )

        recyclerNews.layoutManager = LinearLayoutManager(this)
        recyclerNews.adapter = NewsAdapter(newsList)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}