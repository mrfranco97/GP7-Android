package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.databinding.FragmentMyBookingsBinding;

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
        adapter = new MyBookingsAdapter();
        binding.recyclerMyBookings.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
