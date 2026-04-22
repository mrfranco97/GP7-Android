package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.data.network.ExperienceApi;
import com.example.xplorenow_android.databinding.FragmentExperienceDetailBinding;

import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ExperienceDetailFragment extends Fragment {

    private FragmentExperienceDetailBinding binding;
    private int experienceId;
    private Experience currentExperience;

    @Inject
    ExperienceApi experienceApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExperienceDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            experienceId = getArguments().getInt("experienceId");
        }

        setupToolbar();
        setupBookingButton();
        
        if (experienceId != 0) {
            fetchExperienceDetail(experienceId);
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void setupBookingButton() {
        binding.btnBookNow.setOnClickListener(v -> {
            if (currentExperience != null) {
                BookingBottomSheetFragment bookingSheet = BookingBottomSheetFragment.newInstance(
                        experienceId, currentExperience.getAvailableDate());
                bookingSheet.show(getChildFragmentManager(), bookingSheet.getTag());
            }
        });
    }

    private void fetchExperienceDetail(int id) {
        binding.progressDetail.setVisibility(View.VISIBLE);
        experienceApi.getExperienceDetail(String.valueOf(id)).enqueue(new Callback<Experience>() {
            @Override
            public void onResponse(Call<Experience> call, Response<Experience> response) {
                if (isAdded()) {
                    binding.progressDetail.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        currentExperience = response.body();
                        bindExperienceData(currentExperience);
                    } else {
                        Toast.makeText(getContext(), "Error al cargar el detalle", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Experience> call, Throwable t) {
                if (isAdded()) {
                    binding.progressDetail.setVisibility(View.GONE);
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
