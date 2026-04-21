package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.data.model.TimeSlot;
import com.example.xplorenow_android.databinding.LayoutBookingBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingBottomSheetFragment extends BottomSheetDialogFragment {

    private LayoutBookingBottomSheetBinding binding;
    private ExperienceViewModel viewModel;
    private int experienceId;
    private String selectedDate;
    private String selectedTimeSlot;
    private int participants = 1;
    private int maxAvailableSpots = 0;
    private DateGridAdapter dateAdapter;

    public static BookingBottomSheetFragment newInstance(int experienceId) {
        BookingBottomSheetFragment fragment = new BookingBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("experienceId", experienceId);
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
        viewModel = new ViewModelProvider(requireActivity()).get(ExperienceViewModel.class);
        if (getArguments() != null) {
            experienceId = getArguments().getInt("experienceId");
        }

        setupDateGrid();
        setupListeners();
        observeViewModel();
        
        Experience exp = viewModel.getExperienceDetailLiveData().getValue();
        if (exp != null && exp.getAvailableDate() != null) {
            applyInitialDate(exp.getAvailableDate());
        } else {
            viewModel.getExperienceDetailLiveData().observe(getViewLifecycleOwner(), this::onExperienceLoaded);
        }
    }

    private void onExperienceLoaded(Experience exp) {
        if (exp != null && exp.getAvailableDate() != null) {
            applyInitialDate(exp.getAvailableDate());
        }
    }

    private void applyInitialDate(String fullDate) {
        String date = fullDate.split("T")[0];
        selectedDate = date;
        dateAdapter.setStartDate(date);
        viewModel.fetchAvailability(experienceId, date);
    }

    private void setupDateGrid() {
        dateAdapter = new DateGridAdapter(date -> {
            selectedDate = date;
            resetBookingSelection();
            viewModel.fetchAvailability(experienceId, selectedDate);
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
                viewModel.createBooking(experienceId, selectedDate, selectedTimeSlot, participants);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getAvailabilityLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getExperienceId() == experienceId) {
                displayTimeSlots(response.getSlots());
            }
        });

        viewModel.getBookingResultLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                if (response.getStatus() != null && response.getStatus().equals("confirmed")) {
                    Toast.makeText(getContext(), "¡Reserva confirmada!", Toast.LENGTH_LONG).show();
                    viewModel.fetchExperienceDetail(experienceId);
                    viewModel.clearBookingResult();
                    dismiss();
                } else if (response.getMessage() != null) {
                    Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_LONG).show();
                    viewModel.clearBookingResult();
                }
            }
        });

        viewModel.getBookingErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearBookingResult();
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
