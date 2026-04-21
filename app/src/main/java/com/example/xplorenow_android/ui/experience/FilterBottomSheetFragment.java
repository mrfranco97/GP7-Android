package com.example.xplorenow_android.ui.experience;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.xplorenow_android.databinding.LayoutFilterBottomSheetBinding;
import com.example.xplorenow_android.data.network.Category;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.Calendar;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private LayoutFilterBottomSheetBinding binding;
    private ExperienceViewModel viewModel;
    private String selectedDate = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFilterBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ExperienceViewModel.class);

        setupInitialValues();
        observeCategories();
        setupListeners();
    }

    private void setupInitialValues() {
        ExperienceFilters filters = viewModel.getFilters();
        if (filters != null) {
            binding.editDestination.setText(filters.getDestination());
            if (filters.getMinPrice() != null) binding.editMinPrice.setText(String.valueOf(filters.getMinPrice()));
            if (filters.getMaxPrice() != null) binding.editMaxPrice.setText(String.valueOf(filters.getMaxPrice()));
            if (filters.getDate() != null) {
                selectedDate = filters.getDate();
                binding.btnSelectDate.setText(selectedDate);
            }
        }
    }

    private void observeCategories() {
        viewModel.getCategoriesCatalogLiveData().observe(getViewLifecycleOwner(), categories -> {
            binding.categoryChipGroup.removeAllViews();
            
            Chip allChip = new Chip(getContext());
            allChip.setText("Todos");
            allChip.setCheckable(true);
            allChip.setTag("All");
            if (viewModel.getFilters() != null && "All".equals(viewModel.getFilters().getCategory())) allChip.setChecked(true);
            binding.categoryChipGroup.addView(allChip);

            if (categories != null) {
                for (Category cat : categories) {
                    Chip chip = new Chip(getContext());
                    chip.setText(cat.getLabel());
                    chip.setTag(cat.getKey());
                    chip.setCheckable(true);
                    if (viewModel.getFilters() != null && cat.getKey().equals(viewModel.getFilters().getCategory())) chip.setChecked(true);
                    binding.categoryChipGroup.addView(chip);
                }
            }
        });
    }

    private void setupListeners() {
        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());

        binding.btnApplyFilters.setOnClickListener(v -> {
            String destination = binding.editDestination.getText().toString();
            if (destination.isEmpty()) destination = null;

            String category = "All";
            int checkedId = binding.categoryChipGroup.getCheckedChipId();
            if (checkedId != View.NO_ID) {
                Chip checkedChip = binding.categoryChipGroup.findViewById(checkedId);
                category = (String) checkedChip.getTag();
            }

            Integer minPrice = null;
            try {
                String minStr = binding.editMinPrice.getText().toString();
                if (!minStr.isEmpty()) minPrice = Integer.parseInt(minStr);
            } catch (NumberFormatException ignored) {}

            Integer maxPrice = null;
            try {
                String maxStr = binding.editMaxPrice.getText().toString();
                if (!maxStr.isEmpty()) maxPrice = Integer.parseInt(maxStr);
            } catch (NumberFormatException ignored) {}

            viewModel.applyFilters(destination, category, selectedDate, minPrice, maxPrice);
            dismiss();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            binding.btnSelectDate.setText(selectedDate);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
