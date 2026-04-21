package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.databinding.FragmentExperienceDetailBinding;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExperienceDetailFragment extends Fragment {

    private FragmentExperienceDetailBinding binding;
    private ExperienceViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExperienceDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ExperienceViewModel.class);

        int experienceId = 0;
        if (getArguments() != null) {
            experienceId = getArguments().getInt("experienceId");
        }

        setupToolbar();
        observeViewModel();
        
        if (experienceId != 0) {
            viewModel.fetchExperienceDetail(experienceId);
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void observeViewModel() {
        viewModel.getExperienceDetailLiveData().observe(getViewLifecycleOwner(), experience -> {
            if (experience != null) {
                bindExperienceData(experience);
            }
        });

        viewModel.getDetailErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindExperienceData(Experience exp) {
        binding.textDetailName.setText(exp.getName());
        binding.textDetailDestination.setText(exp.getDestination());
        binding.textDetailDescription.setText(exp.getDescription());
        binding.textDetailPrice.setText(String.format(Locale.getDefault(), "$%.0f", exp.getPrice()));

        if (exp.getIncludes() != null) {
            StringBuilder sb = new StringBuilder();
            for (String item : exp.getIncludes()) {
                sb.append("• ").append(item).append("\n");
            }
            binding.textDetailIncludes.setText(sb.toString().trim());
        }

        binding.textDetailMeetingPoint.setText("📍 Punto de encuentro: " + exp.getMeetingPoint());
        if (exp.getAssignedGuide() != null) {
            binding.textDetailGuide.setText("👤 Guía: " + exp.getAssignedGuide().getName());
        }
        binding.textDetailDuration.setText("🕒 Duración: " + exp.getDuration());
        binding.textDetailLanguage.setText("🌐 Idioma: " + exp.getLanguage());

        if (exp.getCancellationPolicy() != null) {
            binding.textDetailCancellation.setText(exp.getCancellationPolicy());
        } else {
            binding.textDetailCancellation.setText("Sin política de cancelación especificada.");
        }

        Glide.with(this)
                .load(exp.getImageUrl())
                .into(binding.imageDetailMain);
                
        if (exp.getGallery() == null || exp.getGallery().isEmpty()) {
            binding.textGalleryTitle.setVisibility(View.GONE);
            binding.recyclerGallery.setVisibility(View.GONE);
        } else {
            binding.textGalleryTitle.setVisibility(View.VISIBLE);
            binding.recyclerGallery.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
