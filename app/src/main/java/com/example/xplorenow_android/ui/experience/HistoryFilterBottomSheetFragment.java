package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.xplorenow_android.databinding.LayoutHistoryFilterBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistoryFilterBottomSheetFragment extends BottomSheetDialogFragment {

    private LayoutHistoryFilterBottomSheetBinding binding;
    private String dateFrom = null;
    private String dateTo = null;
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface FilterListener {
        void onFiltersApplied(String dateFrom, String dateTo, String destination);
    }

    private FilterListener listener;

    public void setListener(FilterListener listener) {
        this.listener = listener;
    }

    public void setInitialFilters(String dateFrom, String dateTo, String destination) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        // destination is set in onViewCreated
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutHistoryFilterBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        
        // Set initial destination if provided (passed via arguments or field)
        // For simplicity, we could pass it here if we had it.
    }

    private void setupListeners() {
        binding.btnDateFrom.setOnClickListener(v -> showDatePicker(true));
        binding.btnDateTo.setOnClickListener(v -> showDatePicker(false));

        binding.btnApplyFilters.setOnClickListener(v -> {
            String destination = binding.editDestination.getText().toString().trim();
            if (listener != null) {
                listener.onFiltersApplied(dateFrom, dateTo, destination.isEmpty() ? null : destination);
            }
            dismiss();
        });
    }

    private void showDatePicker(boolean isFrom) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isFrom ? "Fecha desde" : "Fecha hasta")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedApi = apiDateFormat.format(date);
            String formattedDisplay = displayDateFormat.format(date);

            if (isFrom) {
                dateFrom = formattedApi;
                binding.btnDateFrom.setText(formattedDisplay);
            } else {
                dateTo = formattedApi;
                binding.btnDateTo.setText(formattedDisplay);
            }
        });

        datePicker.show(getChildFragmentManager(), "DATE_PICKER");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
