package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.data.local.BookingDao;
import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.BookingCancellationResponse;
import com.example.xplorenow_android.data.network.MyBookingsResponse;
import com.example.xplorenow_android.databinding.FragmentMyBookingsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class MyBookingsFragment extends Fragment {

    private FragmentMyBookingsBinding binding;
    private MyBookingsAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Inject
    BookingApi bookingApi;

    @Inject
    BookingDao bookingDao;

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
        adapter = new MyBookingsAdapter(new MyBookingsAdapter.OnBookingActionListener() {
            @Override
            public void onCancelClick(Booking booking) {
                showCancelConfirmation(booking);
            }

            @Override
            public void onRateClick(Booking booking) {
                showRatingBottomSheet(booking);
            }
        });
        binding.recyclerMyBookings.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void fetchMyBookings() {
        binding.progressMyBookings.setVisibility(View.VISIBLE);
        
        bookingApi.getMyBookings().enqueue(new Callback<MyBookingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyBookingsResponse> call, @NonNull Response<MyBookingsResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Booking> bookings = response.body().getItems();
                        executor.execute(() -> {
                            bookingDao.deleteAll();
                            bookingDao.insertBookings(bookings);
                            mainHandler.post(() -> {
                                binding.progressMyBookings.setVisibility(View.GONE);
                                displayBookings(bookings);
                            });
                        });
                    } else {
                        loadFromLocal();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyBookingsResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    loadFromLocal();
                }
            }
        });
    }

    private void loadFromLocal() {
        executor.execute(() -> {
            List<Booking> bookings = bookingDao.getAllBookings();
            mainHandler.post(() -> {
                if (isAdded()) {
                    binding.progressMyBookings.setVisibility(View.GONE);
                    if (bookings != null && !bookings.isEmpty()) {
                        displayBookings(bookings);
                    } else {
                        displayBookings(null);
                        Snackbar.make(binding.getRoot(), "No hay datos disponibles sin conexión", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
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

    private void showRatingBottomSheet(Booking booking) {
        RatingBottomSheetFragment fragment = RatingBottomSheetFragment.newInstance(String.valueOf(booking.getId()));
        fragment.setOnRatingSubmittedListener(this::fetchMyBookings);
        fragment.show(getChildFragmentManager(), "RatingBottomSheet");
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
