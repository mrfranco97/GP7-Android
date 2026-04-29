package com.example.xplorenow_android.ui.experience;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.databinding.ItemExperienceBinding;

import java.util.Locale;

public class ExperienceAdapter extends PagingDataAdapter<Experience, ExperienceAdapter.ExperienceViewHolder> {

    public interface OnExperienceClickListener {
        void onExperienceClick(Experience experience);
    }

    private final OnExperienceClickListener listener;

    public ExperienceAdapter(OnExperienceClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExperienceBinding binding = ItemExperienceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExperienceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienceViewHolder holder, int position) {
        Experience item = getItem(position);
        if (item != null) {
            holder.bind(item, listener);
        }
    }

    public static class ExperienceViewHolder extends RecyclerView.ViewHolder {
        private final ItemExperienceBinding binding;

        public ExperienceViewHolder(ItemExperienceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Experience item, OnExperienceClickListener listener) {
            binding.textName.setText(item.getName());
            binding.textDestination.setText(item.getDestination());
            
            // Usar categoryLabel si está disponible, de lo contrario usar category
            String categoryDisplay = (item.getCategoryLabel() != null && !item.getCategoryLabel().isEmpty()) 
                    ? item.getCategoryLabel() : item.getCategory();
            binding.textCategory.setText(categoryDisplay);

            binding.textDuration.setText(item.getDuration());
            binding.textPrice.setText(String.format(Locale.getDefault(), "$%.0f", item.getPrice()));
            binding.textSpots.setText(String.format(Locale.getDefault(), "%d cupos disponibles", item.getAvailableSpots()));

            Glide.with(binding.imageActivity.getContext())
                    .load(item.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(new ColorDrawable(Color.parseColor("#F0F2F5")))
                    .into(binding.imageActivity);

            binding.btnFavorite.setOnClickListener(v -> {
                boolean isSelected = !v.isSelected();
                v.setSelected(isSelected);
                if (isSelected) {
                    binding.btnFavorite.setColorFilter(Color.BLACK);
                } else {
                    binding.btnFavorite.setColorFilter(Color.parseColor("#757575")); // text_secondary color
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExperienceClick(item);
                }
            });
        }
    }

    private static final DiffUtil.ItemCallback<Experience> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Experience>() {
                @Override
                public boolean areItemsTheSame(@NonNull Experience oldItem, @NonNull Experience newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Experience oldItem, @NonNull Experience newItem) {
                    return oldItem.getName().equals(newItem.getName()) &&
                            oldItem.getDestination().equals(newItem.getDestination()) &&
                            oldItem.getPrice() == newItem.getPrice() &&
                            oldItem.getAvailableSpots() == newItem.getAvailableSpots();
                }
            };
}
