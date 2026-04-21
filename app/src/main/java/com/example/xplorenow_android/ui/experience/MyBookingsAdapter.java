package com.example.xplorenow_android.ui.experience;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.databinding.ItemBookingBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyBookingsAdapter extends RecyclerView.Adapter<MyBookingsAdapter.BookingViewHolder> {

    public interface OnBookingCancelListener {
        void onCancelClick(Booking booking);
    }

    private final List<Booking> items = new ArrayList<>();
    private final OnBookingCancelListener cancelListener;

    public MyBookingsAdapter(OnBookingCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setItems(List<Booking> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingBinding binding = ItemBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(items.get(position), cancelListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingBinding binding;

        BookingViewHolder(ItemBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Booking booking, OnBookingCancelListener listener) {
            binding.textBookingName.setText(booking.getExperience().getName());
            binding.textBookingStatus.setText(booking.getStatus().toUpperCase());
            binding.textBookingPrice.setText(String.format(Locale.getDefault(), "$%.0f", booking.getTotalPrice()));
            binding.textBookingParticipants.setText(String.format(Locale.getDefault(), "%d personas", booking.getParticipants()));
            
            String dateStr = booking.getDate().split("T")[0];
            binding.textBookingDateTime.setText(String.format("%s • %s", dateStr, booking.getTimeSlot()));

            setStatusBadgeColor(booking.getStatus());

            // Solo mostrar botón cancelar si está confirmada
            binding.btnCancelBooking.setVisibility(
                    "confirmada".equalsIgnoreCase(booking.getStatus()) ? View.VISIBLE : View.GONE
            );

            binding.btnCancelBooking.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelClick(booking);
                }
            });

            Glide.with(binding.imageBookingExperience.getContext())
                    .load(booking.getExperience().getImageUrl())
                    .centerCrop()
                    .into(binding.imageBookingExperience);
        }

        private void setStatusBadgeColor(String status) {
            int color;
            switch (status.toLowerCase()) {
                case "confirmada":
                    color = Color.parseColor("#00B894"); // Green
                    break;
                case "cancelada":
                    color = Color.parseColor("#D63031"); // Red
                    break;
                case "finalizada":
                    color = Color.parseColor("#636E72"); // Grey
                    break;
                default:
                    color = Color.parseColor("#0984E3"); // Blue
            }
            GradientDrawable drawable = (GradientDrawable) binding.textBookingStatus.getBackground();
            if (drawable != null) {
                drawable.setColor(color);
            }
        }
    }
}
