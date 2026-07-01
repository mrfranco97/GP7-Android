package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.databinding.FragmentQrScannerBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class QrScannerFragment extends Fragment {

    private FragmentQrScannerBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQrScannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // bookingId is available via getArguments().getString("bookingId")
        // for Persona 3 to use when implementing the actual QR scanner
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
