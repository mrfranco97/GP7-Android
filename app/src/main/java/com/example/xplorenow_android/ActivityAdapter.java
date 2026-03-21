package com.example.xplorenow_android;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.databinding.ItemActivityBinding;

import java.util.Locale;

public class ActivityAdapter extends PagingDataAdapter<ActivityItem, ActivityAdapter.ActivityViewHolder> {

    public ActivityAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActivityBinding binding = ItemActivityBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ActivityViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityItem item = getItem(position);
        if (item != null) {
            holder.bind(item);
        }
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        private final ItemActivityBinding binding;

        public ActivityViewHolder(ItemActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ActivityItem item) {
            binding.textName.setText(item.getName());
            binding.textDestination.setText(item.getDestination());
            binding.textCategory.setText(item.getCategory());
            binding.textDuration.setText(item.getDuration());
            binding.textPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice()));
            binding.textSpots.setText(String.format(Locale.getDefault(), "%d cupos disponibles", item.getAvailableSpots()));

            Glide.with(binding.imageActivity.getContext())
                    .load(item.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.imageActivity);
        }
    }

    private static final DiffUtil.ItemCallback<ActivityItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ActivityItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull ActivityItem oldItem, @NonNull ActivityItem newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ActivityItem oldItem, @NonNull ActivityItem newItem) {
                    return oldItem.getName().equals(newItem.getName()) &&
                            oldItem.getDestination().equals(newItem.getDestination());
                }
            };
}
