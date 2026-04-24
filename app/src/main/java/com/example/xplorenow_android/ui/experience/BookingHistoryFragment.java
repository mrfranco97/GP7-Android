package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.BookingHistoryResponse;
import com.example.xplorenow_android.databinding.FragmentBookingHistoryBinding;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class BookingHistoryFragment extends Fragment implements HistoryFilterBottomSheetFragment.FilterListener {

    private FragmentBookingHistoryBinding binding;
    private BookingHistoryAdapter adapter;

    private String dateFrom = null;
    private String dateTo = null;
    private String destination = null;

    @Inject
    BookingApi bookingApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupFilters();
        setupListeners();
        fetchHistory();
    }

    private void setupRecyclerView() {
        adapter = new BookingHistoryAdapter();
        adapter.setOnItemClickListener(item -> {
            Bundle args = new Bundle();
            args.putString("bookingId", String.valueOf(item.getId()));
            Navigation.findNavController(requireView()).navigate(
                    R.id.action_BookingHistoryFragment_to_BookingDetailFragment, args);
        });
        binding.recyclerHistory.setAdapter(adapter);
    }

    private void setupFilters() {
        binding.btnShowFilters.setOnClickListener(v -> {
            HistoryFilterBottomSheetFragment bottomSheet = new HistoryFilterBottomSheetFragment();
            bottomSheet.setInitialFilters(dateFrom, dateTo, destination);
            bottomSheet.setListener(this);
            bottomSheet.show(getChildFragmentManager(), "HistoryFilterBottomSheet");
        });
        
        binding.btnClearFilters.setOnClickListener(v -> {
            dateFrom = null;
            dateTo = null;
            destination = null;
            binding.btnClearFilters.setVisibility(View.GONE);
            fetchHistory();
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    @Override
    public void onFiltersApplied(String dateFrom, String dateTo, String destination) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.destination = destination;
        
        boolean hasFilters = dateFrom != null || dateTo != null || destination != null;
        binding.btnClearFilters.setVisibility(hasFilters ? View.VISIBLE : View.GONE);
        
        fetchHistory();
    }

    private void fetchHistory() {
        binding.progressHistory.setVisibility(View.VISIBLE);
        bookingApi.getBookingHistory(dateFrom, dateTo, destination).enqueue(new Callback<BookingHistoryResponse>() {
            @Override
            public void onResponse(Call<BookingHistoryResponse> call, Response<BookingHistoryResponse> response) {
                if (isAdded()) {
                    binding.progressHistory.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        displayHistory(response.body());
                    } else {
                        showError("Error al cargar el historial");
                    }
                }
            }

            @Override
            public void onFailure(Call<BookingHistoryResponse> call, Throwable t) {
                if (isAdded()) {
                    binding.progressHistory.setVisibility(View.GONE);
                    showError(t.getMessage());
                }
            }
        });
    }

    private void displayHistory(BookingHistoryResponse response) {
        if (response.getItems() != null && !response.getItems().isEmpty()) {
            adapter.setItems(response.getItems());
            binding.recyclerHistory.setVisibility(View.VISIBLE);
            binding.textNoHistory.setVisibility(View.GONE);
        } else {
            adapter.setItems(new java.util.ArrayList<>());
            binding.recyclerHistory.setVisibility(View.GONE);
            binding.textNoHistory.setVisibility(View.VISIBLE);
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
