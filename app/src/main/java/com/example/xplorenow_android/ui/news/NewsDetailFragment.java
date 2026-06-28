package com.example.xplorenow_android.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewsDetailFragment extends Fragment {

    public static final String ARG_TITLE = "extra_title";
    public static final String ARG_DESCRIPTION = "extra_description";
    public static final String ARG_IMAGE_URL = "extra_image_url";
    public static final String ARG_DATE = "extra_date";
    public static final String ARG_EXPERIENCE_ID = "extra_experience_id";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_detail, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbarNewsDetail);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString(ARG_TITLE);
            String description = args.getString(ARG_DESCRIPTION);
            String imageUrl = args.getString(ARG_IMAGE_URL);
            String date = args.getString(ARG_DATE);
            int experienceId = args.getInt(ARG_EXPERIENCE_ID, -1);

            TextView tvTitle = view.findViewById(R.id.tvNewsDetailTitle);
            TextView tvDescription = view.findViewById(R.id.tvNewsDetailDescription);
            TextView tvDate = view.findViewById(R.id.tvNewsDetailDate);
            ImageView ivImage = view.findViewById(R.id.ivNewsDetailImage);
            MaterialButton btnViewExperience = view.findViewById(R.id.btnViewExperience);

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
                    Bundle bundle = new Bundle();
                    bundle.putInt("experienceId", experienceId);
                    Navigation.findNavController(v).navigate(R.id.action_NewsDetailFragment_to_ExperienceDetailFragment, bundle);
                });
            } else {
                btnViewExperience.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private String formatPublishedDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = parser.parse(dateStr);

            if (date != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("d 'de' MMMM, yyyy", new Locale("es", "ES"));
                return formatter.format(date);
            }
        } catch (Exception e) {
            try {
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
