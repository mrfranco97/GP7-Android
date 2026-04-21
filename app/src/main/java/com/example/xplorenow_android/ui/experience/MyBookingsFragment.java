package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.databinding.FragmentMyBookingsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyBookingsFragment extends Fragment {

    private FragmentMyBookingsBinding binding;
    private ExperienceViewModel viewModel;
    private MyBookingsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ExperienceViewModel.class);

        setupRecyclerView();
        setupListeners();
        observeViewModel();

        viewModel.fetchMyBookings();
    }

    private void setupRecyclerView() {
        adapter = new MyBookingsAdapter(this::showCancelConfirmation);
        binding.recyclerMyBookings.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void showCancelConfirmation(Booking booking) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("¿Cancelar reserva?")
                .setMessage("¿Estás seguro de que deseas cancelar tu reserva para " + booking.getExperience().getName() + "?")
                .setNegativeButton("No, mantener", null)
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    viewModel.cancelBooking(String.valueOf(booking.getId()));
                })
                .show();
    }

    private void observeViewModel() {
        viewModel.getMyBookingsLiveData().observe(getViewLifecycleOwner(), bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                adapter.setItems(bookings);
                binding.recyclerMyBookings.setVisibility(View.VISIBLE);
                binding.textNoBookings.setVisibility(View.GONE);
            } else {
                binding.recyclerMyBookings.setVisibility(View.GONE);
                binding.textNoBookings.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getMyBookingsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressMyBookings.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getCancellationResultLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                showCancellationDetails(response);
                viewModel.clearCancellationResult();
            }
        });

        viewModel.getBookingErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
                viewModel.clearBookingResult();
            }
        });
    }

    private void showCancellationDetails(com.example.xplorenow_android.data.network.BookingCancellationResponse response) {
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
