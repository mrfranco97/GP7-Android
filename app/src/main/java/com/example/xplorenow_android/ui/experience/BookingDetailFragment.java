package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.databinding.FragmentBookingDetailBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class BookingDetailFragment extends Fragment {

    private FragmentBookingDetailBinding binding;

    @Inject
    BookingApi bookingApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        String bookingId = getArguments() != null ? getArguments().getString("bookingId") : null;
        if (bookingId != null) {
            fetchBookingDetail(bookingId);
        } else {
            Navigation.findNavController(requireView()).navigateUp();
        }

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void fetchBookingDetail(String id) {
        binding.progressDetail.setVisibility(View.VISIBLE);
        bookingApi.getBookingDetail(id).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (isAdded()) {
                    binding.progressDetail.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        displayDetail(response.body());
                    } else {
                        showError("Error al cargar el detalle");
                    }
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                if (isAdded()) {
                    binding.progressDetail.setVisibility(View.GONE);
                    showError(t.getMessage());
                }
            }
        });
    }

    private void displayDetail(Booking booking) {
        Booking.ActivityDetail activity = booking.getActivity();
        
        binding.textBookingId.setText(String.format(Locale.getDefault(), "Reserva #%s", booking.getId()));
        binding.textActivityName.setText(activity.getName());
        binding.textDestination.setText(activity.getDestination());
        binding.textDescription.setText(activity.getDescription());
        
        String info = String.format(Locale.getDefault(),
                "Fecha: %s\nHorario: %s\nParticipantes: %d personas\nTotal: $%.0f",
                booking.getDate(),
                booking.getTimeSlot(),
                booking.getParticipants(),
                booking.getTotalPrice());
        binding.textBookingInfo.setText(info);

        Glide.with(this)
                .load(activity.getImageUrl())
                .into(binding.imageActivity);

        if (booking.getRating() != null) {
            binding.labelRating.setVisibility(View.VISIBLE);
            binding.layoutRatingDetails.setVisibility(View.VISIBLE);
            binding.ratingActivity.setRating(booking.getRating().getActivityStars());
            binding.ratingGuide.setRating(booking.getRating().getGuideStars());
            binding.textComment.setText(booking.getRating().getComment());
        }
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
