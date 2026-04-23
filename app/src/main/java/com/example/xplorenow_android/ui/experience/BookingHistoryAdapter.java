package com.example.xplorenow_android.ui.experience;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xplorenow_android.data.model.BookingHistoryItem;
import com.example.xplorenow_android.databinding.ItemBookingHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private List<BookingHistoryItem> items = new ArrayList<>();

    public void setItems(List<BookingHistoryItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingHistoryBinding binding = ItemBookingHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingHistoryBinding binding;

        ViewHolder(ItemBookingHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BookingHistoryItem item) {
            binding.textHistoryDate.setText(item.getDate());
            binding.textHistoryName.setText(item.getActivity().getName());
            binding.textHistoryDestination.setText(item.getActivity().getDestination());
            binding.textHistoryDuration.setText("• " + item.getActivity().getDuration());
            binding.textHistoryGuide.setText(item.getGuide().getName());
            
            if (item.getRating() != null) {
                binding.layoutRatings.setVisibility(View.VISIBLE);
                binding.ratingActivity.setRating(item.getRating().getActivityStars());
                binding.ratingGuide.setRating(item.getRating().getGuideStars());
                
                if (item.getRating().getComment() != null && !item.getRating().getComment().isEmpty()) {
                    binding.textHistoryComment.setVisibility(View.VISIBLE);
                    binding.textHistoryComment.setText(item.getRating().getComment());
                } else {
                    binding.textHistoryComment.setVisibility(View.GONE);
                }
            } else {
                binding.layoutRatings.setVisibility(View.GONE);
            }
        }
    }
}
