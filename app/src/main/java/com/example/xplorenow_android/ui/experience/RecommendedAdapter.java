package com.example.xplorenow_android.ui.experience;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.databinding.ItemRecommendedExperienceBinding;

import java.util.ArrayList;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.RecommendedViewHolder> {

    public interface OnRecommendedClickListener {
        void onExperienceClick(Experience experience);
        void onFavoriteClick(Experience experience);
    }

    private final List<Experience> items = new ArrayList<>();
    private final OnRecommendedClickListener listener;

    public RecommendedAdapter(OnRecommendedClickListener listener) {
        this.listener = listener;
    }

    public List<Experience> getItems() {
        return items;
    }

    public void setItems(List<Experience> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecommendedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecommendedExperienceBinding binding = ItemRecommendedExperienceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RecommendedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedViewHolder holder, int position) {
        Experience item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RecommendedViewHolder extends RecyclerView.ViewHolder {
        final ItemRecommendedExperienceBinding binding;

        RecommendedViewHolder(ItemRecommendedExperienceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Experience item, OnRecommendedClickListener listener) {
            binding.textRecommendedName.setText(item.getName());
            binding.textRecommendedDestination.setText(item.getDestination());
            
            Glide.with(binding.imageRecommended.getContext())
                    .load(item.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.imageRecommended);

            binding.btnFavorite.setSelected(item.isFavorite());
            if (item.isFavorite()) {
                binding.btnFavorite.setColorFilter(android.graphics.Color.BLACK);
            } else {
                binding.btnFavorite.setColorFilter(android.graphics.Color.parseColor("#757575"));
            }

            binding.btnFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(item);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExperienceClick(item);
                }
            });
        }
    }
}
