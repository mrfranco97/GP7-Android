package com.example.xplorenow_android.ui.news

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xplorenow_android.R
import com.example.xplorenow_android.data.model.News

class NewsAdapter(private val list: List<News>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgNews)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val desc: TextView = view.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = list[position]

        holder.title.text = news.title
        holder.desc.text = news.description
        val images = listOf(
            R.drawable.news_promo,
            R.drawable.news_mendoza,
            R.drawable.news_bsas
        )

        holder.image.setImageResource(images[position % images.size])

        holder.itemView.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle(news.title)
                .setMessage(news.description + "\n\nDetalle completo de la noticia.")
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }
}