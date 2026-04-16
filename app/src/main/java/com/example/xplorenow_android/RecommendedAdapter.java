package com.example.xplorenow_android;

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

    private final List<Experience> items = new ArrayList<>();

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
        holder.binding.textRecommendedName.setText(item.getName());
        holder.binding.textRecommendedDestination.setText(item.getDestination());
        
        Glide.with(holder.binding.imageRecommended.getContext())
                .load(item.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.binding.imageRecommended);
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
    }
}
