package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.BookingCancellationResponse;
import com.example.xplorenow_android.data.network.MyBookingsResponse;
import com.example.xplorenow_android.databinding.FragmentMyBookingsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class MyBookingsFragment extends Fragment {

    private FragmentMyBookingsBinding binding;
    private MyBookingsAdapter adapter;

    @Inject
    BookingApi bookingApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupListeners();
        fetchMyBookings();
    }

    private void setupRecyclerView() {
        adapter = new MyBookingsAdapter(this::showCancelConfirmation);
        binding.recyclerMyBookings.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void fetchMyBookings() {
        binding.progressMyBookings.setVisibility(View.VISIBLE);
        bookingApi.getMyBookings().enqueue(new Callback<MyBookingsResponse>() {
            @Override
            public void onResponse(Call<MyBookingsResponse> call, Response<MyBookingsResponse> response) {
                if (isAdded()) {
                    binding.progressMyBookings.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        displayBookings(response.body().getItems());
                    }
                }
            }

            @Override
            public void onFailure(Call<MyBookingsResponse> call, Throwable t) {
                if (isAdded()) {
                    binding.progressMyBookings.setVisibility(View.GONE);
                }
            }
        });
    }

    private void displayBookings(List<Booking> bookings) {
        if (bookings != null && !bookings.isEmpty()) {
            adapter.setItems(bookings);
            binding.recyclerMyBookings.setVisibility(View.VISIBLE);
            binding.textNoBookings.setVisibility(View.GONE);
        } else {
            binding.recyclerMyBookings.setVisibility(View.GONE);
            binding.textNoBookings.setVisibility(View.VISIBLE);
        }
    }

    private void showCancelConfirmation(Booking booking) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("¿Cancelar reserva?")
                .setMessage("¿Estás seguro de que deseas cancelar tu reserva para " + booking.getExperience().getName() + "?")
                .setNegativeButton("No, mantener", null)
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    cancelBooking(String.valueOf(booking.getId()));
                })
                .show();
    }

    private void cancelBooking(String bookingId) {
        binding.progressMyBookings.setVisibility(View.VISIBLE);
        bookingApi.cancelBooking(bookingId).enqueue(new Callback<BookingCancellationResponse>() {
            @Override
            public void onResponse(Call<BookingCancellationResponse> call, Response<BookingCancellationResponse> response) {
                if (isAdded()) {
                    binding.progressMyBookings.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        showCancellationDetails(response.body());
                        fetchMyBookings();
                    } else {
                        Snackbar.make(binding.getRoot(), "Error al cancelar la reserva", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BookingCancellationResponse> call, Throwable t) {
                if (isAdded()) {
                    binding.progressMyBookings.setVisibility(View.GONE);
                    Snackbar.make(binding.getRoot(), t.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showCancellationDetails(BookingCancellationResponse response) {
        String details = String.format(Locale.getDefault(),
                "%s\n\nReintegro: $%.0f\nCargo: $%.0f\nTotal: $%.0f",
                response.getCancellationPolicy(),
                response.getRefundAmount(),
                response.getCancellationFee(),
                response.getTotalPrice());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Reserva cancelada")
                .setMessage(details)
                .setPositiveButton("Entendido", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
