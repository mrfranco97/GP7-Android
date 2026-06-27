package com.example.xplorenow_android.ui.news;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.model.News;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final List<News> newsList;

    public NewsAdapter(List<News> newsList) {
        this.newsList = newsList;
    }

    public void setItems(List<News> items) {
        newsList.clear();
        if (items != null) {
            newsList.addAll(items);
        }
        notifyDataSetChanged();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView desc;
        TextView date;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgNews);
            title = itemView.findViewById(R.id.tvTitle);
            desc = itemView.findViewById(R.id.tvDesc);
            date = itemView.findViewById(R.id.tvDate);
        }
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);

        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);

        holder.title.setText(news.getTitle());
        holder.desc.setText(news.getSummary() != null ? news.getSummary() : news.getDescription());
        
        String formattedDate = formatPublishedDate(news.getPublishedAt());
        if (holder.date != null) {
            holder.date.setText(formattedDate);
            holder.date.setVisibility(formattedDate != null ? View.VISIBLE : View.GONE);
        }

        Glide.with(holder.image.getContext())
                .load(news.getImageUrl())
                .placeholder(R.drawable.news_promo)
                .error(R.drawable.news_promo)
                .centerCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), NewsDetailActivity.class);
            intent.putExtra(NewsDetailActivity.EXTRA_TITLE, news.getTitle());
            intent.putExtra(NewsDetailActivity.EXTRA_DESCRIPTION, news.getDescription());
            intent.putExtra(NewsDetailActivity.EXTRA_IMAGE_URL, news.getImageUrl());
            intent.putExtra(NewsDetailActivity.EXTRA_DATE, news.getPublishedAt());
            
            Object relatedId = news.getRelatedActivityId();
            if (relatedId != null) {
                try {
                    int id = Integer.parseInt(relatedId.toString());
                    intent.putExtra(NewsDetailActivity.EXTRA_EXPERIENCE_ID, id);
                } catch (NumberFormatException ignored) {}
            }
            
            holder.itemView.getContext().startActivity(intent);
        });
    }

    private String formatPublishedDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            // Maneja el formato ISO 8601 del JSON: "2026-07-10T09:00:00.000Z"
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = parser.parse(dateStr);
            
            if (date != null) {
                // Formato amigable: "10 jul, 2026"
                SimpleDateFormat formatter = new SimpleDateFormat("d MMM, yyyy", new Locale("es", "ES"));
                return formatter.format(date);
            }
        } catch (Exception e) {
            // Fallback para formatos más simples como "2026-07-10"
            try {
                SimpleDateFormat fallbackParser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = fallbackParser.parse(dateStr.split("T")[0]);
                if (date != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("d MMM, yyyy", new Locale("es", "ES"));
                    return formatter.format(date);
                }
            } catch (Exception ignored) {}
        }
        return dateStr;
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}
