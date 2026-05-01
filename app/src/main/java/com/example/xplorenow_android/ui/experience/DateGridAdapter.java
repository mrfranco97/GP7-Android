package com.example.xplorenow_android.ui.experience;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.databinding.ItemDateGridBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateGridAdapter extends RecyclerView.Adapter<DateGridAdapter.DateViewHolder> {

    public interface OnDateClickListener {
        void onDateClick(String dateFormatted);
    }

    private final List<Date> dateList = new ArrayList<>();
    private final OnDateClickListener listener;
    private int selectedPosition = -1;
    private final SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

    public DateGridAdapter(OnDateClickListener listener) {
        this.listener = listener;
    }

    public void setAvailableDates(List<String> availableDates) {
        dateList.clear();
        if (availableDates != null) {
            for (String dateStr : availableDates) {
                try {
                    String cleanDate = dateStr.split("T")[0];
                    Date date = apiFormat.parse(cleanDate);
                    if (date != null) {
                        if (!containsDate(date)) {
                            dateList.add(date);
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        
        dateList.sort(Date::compareTo);
        
        selectedPosition = dateList.isEmpty() ? -1 : 0;
        notifyDataSetChanged();
        
        if (selectedPosition != -1 && listener != null) {
            listener.onDateClick(apiFormat.format(dateList.get(0)));
        }
    }

    private boolean containsDate(Date date) {
        String formatted = apiFormat.format(date);
        for (Date d : dateList) {
            if (apiFormat.format(d).equals(formatted)) return true;
        }
        return false;
    }

    public void selectDate(String dateStr) {
        if (dateStr == null) return;
        String cleanDate = dateStr.split("T")[0];
        for (int i = 0; i < dateList.size(); i++) {
            if (apiFormat.format(dateList.get(i)).equals(cleanDate)) {
                selectedPosition = i;
                notifyDataSetChanged();
                break;
            }
        }
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDateGridBinding binding = ItemDateGridBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        Date date = dateList.get(position);
        holder.binding.textDay.setText(dayFormat.format(date));
        holder.binding.textMonth.setText(monthFormat.format(date).toUpperCase());

        boolean isSelected = (selectedPosition == position);
        holder.binding.cardDate.setStrokeColor(isSelected ? 
                holder.itemView.getContext().getColor(R.color.accent) : 
                holder.itemView.getContext().getColor(R.color.divider));
        
        holder.binding.cardDate.setCardBackgroundColor(isSelected ? 
                Color.parseColor("#EEF5FF") : 
                holder.itemView.getContext().getColor(R.color.surface));

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                selectedPosition = currentPos;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onDateClick(apiFormat.format(dateList.get(selectedPosition)));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        final ItemDateGridBinding binding;

        DateViewHolder(ItemDateGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
