package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
public class BookingHistoryFragment extends Fragment {

    private FragmentBookingHistoryBinding binding;
    private BookingHistoryAdapter adapter;

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
        setupListeners();
        fetchHistory();
    }

    private void setupRecyclerView() {
        adapter = new BookingHistoryAdapter();
        binding.recyclerHistory.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void fetchHistory() {
        binding.progressHistory.setVisibility(View.VISIBLE);
        bookingApi.getBookingHistory(null, null, null).enqueue(new Callback<BookingHistoryResponse>() {
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
