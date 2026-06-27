package com.example.xplorenow_android.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.R;
import com.example.xplorenow_android.ui.MainActivity;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_IMAGE_URL = "extra_image_url";
    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_EXPERIENCE_ID = "extra_experience_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Toolbar toolbar = findViewById(R.id.toolbarNewsDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        String date = getIntent().getStringExtra(EXTRA_DATE);
        int experienceId = getIntent().getIntExtra(EXTRA_EXPERIENCE_ID, -1);

        TextView tvTitle = findViewById(R.id.tvNewsDetailTitle);
        TextView tvDescription = findViewById(R.id.tvNewsDetailDescription);
        TextView tvDate = findViewById(R.id.tvNewsDetailDate);
        ImageView ivImage = findViewById(R.id.ivNewsDetailImage);
        MaterialButton btnViewExperience = findViewById(R.id.btnViewExperience);

        tvTitle.setText(title);
        tvDescription.setText(description);
        
        String formattedDate = formatPublishedDate(date);
        tvDate.setText(formattedDate != null ? "Publicado el: " + formattedDate : "");

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.news_promo)
                .error(R.drawable.news_promo)
                .centerCrop()
                .into(ivImage);

        if (experienceId != -1) {
            btnViewExperience.setVisibility(View.VISIBLE);
            btnViewExperience.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("experienceId", experienceId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        } else {
            btnViewExperience.setVisibility(View.GONE);
        }
    }

    private String formatPublishedDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {

            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = parser.parse(dateStr);
            
            if (date != null) {
                // Formato amigable: "10 de julio, 2026"
                SimpleDateFormat formatter = new SimpleDateFormat("d 'de' MMMM, yyyy", new Locale("es", "ES"));
                return formatter.format(date);
            }
        } catch (Exception e) {
            try {
                // Fallback para "2026-07-10"
                SimpleDateFormat simpleParser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = simpleParser.parse(dateStr.split("T")[0]);
                if (date != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("d 'de' MMMM, yyyy", new Locale("es", "ES"));
                    return formatter.format(date);
                }
            } catch (Exception ignored) {}
        }
        return dateStr;
    }
}
