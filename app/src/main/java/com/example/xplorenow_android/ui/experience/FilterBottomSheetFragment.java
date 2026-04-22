package com.example.xplorenow_android.ui.experience;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.xplorenow_android.databinding.LayoutFilterBottomSheetBinding;
import com.example.xplorenow_android.data.network.Category;
import com.example.xplorenow_android.data.network.CatalogApi;
import com.example.xplorenow_android.data.network.CategoryResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private LayoutFilterBottomSheetBinding binding;
    private String selectedDate = null;
    private ExperienceFilters currentFilters;

    @Inject
    CatalogApi catalogApi;

    public interface FilterListener {
        void onFiltersApplied(ExperienceFilters filters);
    }

    private FilterListener listener;

    public void setListener(FilterListener listener) {
        this.listener = listener;
    }

    public void setInitialFilters(ExperienceFilters filters) {
        this.currentFilters = filters;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFilterBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupInitialValues();
        fetchCategories();
        setupListeners();
    }

    private void setupInitialValues() {
        if (currentFilters != null) {
            binding.editDestination.setText(currentFilters.getDestination());
            if (currentFilters.getMinPrice() != null) binding.editMinPrice.setText(String.valueOf(currentFilters.getMinPrice()));
            if (currentFilters.getMaxPrice() != null) binding.editMaxPrice.setText(String.valueOf(currentFilters.getMaxPrice()));
            if (currentFilters.getDate() != null) {
                selectedDate = currentFilters.getDate();
                binding.btnSelectDate.setText(selectedDate);
            }
        }
    }

    private void fetchCategories() {
        catalogApi.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    displayCategories(response.body().getItems());
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {}
        });
    }

    private void displayCategories(List<Category> categories) {
        binding.categoryChipGroup.removeAllViews();
        
        Chip allChip = new Chip(getContext());
        allChip.setText("Todos");
        allChip.setCheckable(true);
        allChip.setTag("All");
        if (currentFilters != null && "All".equals(currentFilters.getCategory())) allChip.setChecked(true);
        binding.categoryChipGroup.addView(allChip);

        if (categories != null) {
            for (Category cat : categories) {
                Chip chip = new Chip(getContext());
                chip.setText(cat.getLabel());
                chip.setTag(cat.getKey());
                chip.setCheckable(true);
                if (currentFilters != null && cat.getKey().equals(currentFilters.getCategory())) chip.setChecked(true);
                binding.categoryChipGroup.addView(chip);
            }
        }
    }

    private void setupListeners() {
        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());

        binding.btnApplyFilters.setOnClickListener(v -> {
            ExperienceFilters nextFilters = new ExperienceFilters();
            
            String destination = binding.editDestination.getText().toString();
            nextFilters.setDestination(destination.isEmpty() ? null : destination);

            String category = "All";
            int checkedId = binding.categoryChipGroup.getCheckedChipId();
            if (checkedId != View.NO_ID) {
                Chip checkedChip = binding.categoryChipGroup.findViewById(checkedId);
                category = (String) checkedChip.getTag();
            }
            nextFilters.setCategory(category);
            nextFilters.setDate(selectedDate);

            try {
                String minStr = binding.editMinPrice.getText().toString();
                if (!minStr.isEmpty()) nextFilters.setMinPrice(Integer.parseInt(minStr));
            } catch (NumberFormatException ignored) {}

            try {
                String maxStr = binding.editMaxPrice.getText().toString();
                if (!maxStr.isEmpty()) nextFilters.setMaxPrice(Integer.parseInt(maxStr));
            } catch (NumberFormatException ignored) {}

            if (listener != null) {
                listener.onFiltersApplied(nextFilters);
            }
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
