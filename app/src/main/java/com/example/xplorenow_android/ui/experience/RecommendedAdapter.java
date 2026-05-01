package com.example.xplorenow_android.ui.experience;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.databinding.ItemRecommendedExperienceBinding;

import java.util.ArrayList;
import java.util.List;

public class RecommendedAdapter extends ListAdapter<Experience, RecommendedAdapter.RecommendedViewHolder> {

    public interface OnRecommendedClickListener {
        void onExperienceClick(Experience experience);
        void onFavoriteClick(Experience experience);
    }

    private final OnRecommendedClickListener listener;

    public RecommendedAdapter(OnRecommendedClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public void setItems(List<Experience> items) {
        submitList(items == null ? null : new ArrayList<>(items));
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
        Experience item = getItem(position);
        holder.bind(item, listener);
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

    private static final DiffUtil.ItemCallback<Experience> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Experience>() {
                @Override
                public boolean areItemsTheSame(@NonNull Experience oldItem, @NonNull Experience newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Experience oldItem, @NonNull Experience newItem) {
                    return oldItem.getName().equals(newItem.getName()) &&
                            oldItem.getDestination().equals(newItem.getDestination());
                }
            };
}
