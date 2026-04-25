package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.xplorenow_android.data.local.BookingDao;
import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.data.model.BookingRequest;
import com.example.xplorenow_android.data.model.TimeSlot;
import com.example.xplorenow_android.data.network.AvailabilityResponse;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.BookingResponse;
import com.example.xplorenow_android.databinding.LayoutBookingBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class BookingBottomSheetFragment extends BottomSheetDialogFragment {

    private LayoutBookingBottomSheetBinding binding;
    private int experienceId;
    private String selectedDate;
    private String selectedTimeSlot;
    private int participants = 1;
    private int maxAvailableSpots = 0;
    private DateGridAdapter dateAdapter;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    BookingApi bookingApi;

    @Inject
    BookingDao bookingDao;

    public static BookingBottomSheetFragment newInstance(int experienceId, String availableDate) {
        BookingBottomSheetFragment fragment = new BookingBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("experienceId", experienceId);
        args.putString("availableDate", availableDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutBookingBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            experienceId = getArguments().getInt("experienceId");
            selectedDate = getArguments().getString("availableDate");
        }


        setupDateGrid();
        setupListeners();
        
        dateAdapter.setStartDate(selectedDate);
        fetchAvailability(experienceId, selectedDate);
    }

    private void fetchAvailability(int experienceId, String date) {
        bookingApi.getAvailability(String.valueOf(experienceId), date).enqueue(new Callback<AvailabilityResponse>() {
            @Override
            public void onResponse(Call<AvailabilityResponse> call, Response<AvailabilityResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    displayTimeSlots(response.body().getSlots());
                }
            }
            @Override
            public void onFailure(Call<AvailabilityResponse> call, Throwable t) {}
        });
    }

    private void createBooking(int experienceId, String date, String timeSlot, int participants) {
        BookingRequest request = new BookingRequest(String.valueOf(experienceId), date, timeSlot, participants);
        bookingApi.createBooking(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), "¡Reserva confirmada!", Toast.LENGTH_LONG).show();
                        fetchAndSaveBookingDetail(response.body().getId());
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Error al procesar la reserva", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchAndSaveBookingDetail(String bookingId) {
        bookingApi.getBookingDetail(bookingId).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull Response<Booking> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Booking booking = response.body();
                    executor.execute(() -> bookingDao.insertBooking(booking));
                }
            }
            @Override
            public void onFailure(@NonNull Call<Booking> call, @NonNull Throwable t) {}
        });
    }

    private void setupDateGrid() {
        dateAdapter = new DateGridAdapter(date -> {
            selectedDate = date;
            resetBookingSelection();
            fetchAvailability(experienceId, selectedDate);
        });
        binding.recyclerDates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerDates.setAdapter(dateAdapter);
    }

    private void setupListeners() {
        binding.btnRemoveParticipant.setOnClickListener(v -> {
            if (participants > 1) {
                participants--;
                updateParticipantsUI();
            }
        });

        binding.btnAddParticipant.setOnClickListener(v -> {
            if (participants < maxAvailableSpots) {
                participants++;
                updateParticipantsUI();
            } else {
                Toast.makeText(getContext(), "No hay más cupos disponibles", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnConfirmBooking.setOnClickListener(v -> {
            if (selectedDate != null && selectedTimeSlot != null) {
                createBooking(experienceId, selectedDate, selectedTimeSlot, participants);
            }
        });
    }

    private void resetBookingSelection() {
        selectedTimeSlot = null;
        participants = 1;
        binding.textSlotsLabel.setVisibility(View.GONE);
        binding.slotChipGroup.setVisibility(View.GONE);
        binding.slotChipGroup.removeAllViews();
        binding.textParticipantsLabel.setVisibility(View.GONE);
        binding.layoutParticipants.setVisibility(View.GONE);
        binding.btnConfirmBooking.setEnabled(false);
        binding.textAvailableSpotsWarning.setVisibility(View.GONE);
    }

    private void displayTimeSlots(List<TimeSlot> slots) {
        binding.slotChipGroup.removeAllViews();
        if (slots == null || slots.isEmpty()) {
            Toast.makeText(getContext(), "No hay disponibilidad para esta fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.textSlotsLabel.setVisibility(View.VISIBLE);
        binding.slotChipGroup.setVisibility(View.VISIBLE);

        for (TimeSlot slot : slots) {
            Chip chip = new Chip(getContext());
            chip.setText(slot.getTime() + " (" + slot.getAvailableSpots() + " cupos)");
            chip.setCheckable(true);
            chip.setEnabled(slot.getAvailableSpots() > 0);
            
            chip.setOnClickListener(v -> {
                selectedTimeSlot = slot.getTime();
                maxAvailableSpots = slot.getAvailableSpots();
                showParticipantsSection();
            });
            binding.slotChipGroup.addView(chip);
        }
    }

    private void showParticipantsSection() {
        binding.textParticipantsLabel.setVisibility(View.VISIBLE);
        binding.layoutParticipants.setVisibility(View.VISIBLE);
        binding.btnConfirmBooking.setEnabled(true);
        updateParticipantsUI();
    }

    private void updateParticipantsUI() {
        binding.textParticipantsCount.setText(String.valueOf(participants));
        binding.textAvailableSpotsWarning.setText(maxAvailableSpots + " cupos disponibles");
        binding.textAvailableSpotsWarning.setVisibility(View.VISIBLE);
        
        if (participants > maxAvailableSpots) {
            participants = maxAvailableSpots;
            binding.textParticipantsCount.setText(String.valueOf(participants));
        }
        
        binding.btnConfirmBooking.setEnabled(participants > 0 && participants <= maxAvailableSpots);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
