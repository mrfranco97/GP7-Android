package com.example.xplorenow_android.ui.experience;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.data.model.Favorite;
import com.example.xplorenow_android.databinding.ItemExperienceBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Favorite favorite);
        void onRemoveFavorite(Favorite favorite);
    }

    private final List<Favorite> items = new ArrayList<>();
    private final OnFavoriteClickListener listener;

    public FavoritesAdapter(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Favorite> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExperienceBinding binding = ItemExperienceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemExperienceBinding binding;

        public ViewHolder(ItemExperienceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Favorite item, OnFavoriteClickListener listener) {
            binding.textName.setText(item.getActivity().getName());
            binding.textDestination.setText(item.getActivity().getDestination());
            
            String categoryDisplay = (item.getActivity().getCategoryLabel() != null && !item.getActivity().getCategoryLabel().isEmpty())
                    ? item.getActivity().getCategoryLabel() : item.getActivity().getCategory();
            binding.textCategory.setText(categoryDisplay);

            binding.textDuration.setText(item.getActivity().getDuration());
            binding.textPrice.setText(String.format(Locale.getDefault(), "$%.0f", item.getActivity().getPrice()));
            binding.textSpots.setText(String.format(Locale.getDefault(), "%d cupos disponibles", item.getActivity().getAvailableSpots()));

            Glide.with(binding.imageActivity.getContext())
                    .load(item.getActivity().getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(new ColorDrawable(Color.parseColor("#F0F2F5")))
                    .into(binding.imageActivity);

            // In Favorites, it's always selected
            binding.btnFavorite.setSelected(true);
            binding.btnFavorite.setColorFilter(Color.BLACK);

            binding.btnFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveFavorite(item);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(item);
                }
            });
        }
    }
}
