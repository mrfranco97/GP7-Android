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
import java.util.Calendar;
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
        generateDates(null);
    }

    public void setStartDate(String startDateStr) {
        generateDates(startDateStr);
        selectedPosition = 0; // Por defecto seleccionamos la primera disponible
        notifyDataSetChanged();
    }

    private void generateDates(String startDateStr) {
        dateList.clear();
        Calendar calendar = Calendar.getInstance();
        
        if (startDateStr != null) {
            try {
                String cleanDate = startDateStr.split("T")[0];
                Date startDate = apiFormat.parse(cleanDate);
                if (startDate != null) {
                    calendar.setTime(startDate);
                }
            } catch (Exception ignored) {}
        }

        for (int i = 0; i < 14; i++) {
            dateList.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
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

    public String getSelectedDate() {
        if (selectedPosition >= 0 && selectedPosition < dateList.size()) {
            return apiFormat.format(dateList.get(selectedPosition));
        }
        return null;
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
            selectedPosition = holder.getBindingAdapterPosition();
            notifyDataSetChanged();
            if (listener != null) {
                listener.onDateClick(apiFormat.format(date));
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
