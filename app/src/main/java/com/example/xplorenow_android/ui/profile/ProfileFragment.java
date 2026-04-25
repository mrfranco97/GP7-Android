package com.example.xplorenow_android.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.ui.user.UserPreferences;
import com.example.xplorenow_android.data.model.Interest;
import com.example.xplorenow_android.data.model.User;
import com.example.xplorenow_android.data.local.TokenManager;
import com.example.xplorenow_android.data.network.AuthApi;
import com.example.xplorenow_android.data.network.CatalogApi;
import com.example.xplorenow_android.databinding.FragmentProfileBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    
    @Inject
    AuthApi authApi;

    @Inject
    CatalogApi catalogApi;

    @Inject
    TokenManager tokenManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        fetchProfile();
    }

    private void fetchProfile() {
        showLoading(true);
        authApi.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fetchInterests(response.body());
                } else {
                    showLoading(false);
                    showError("Error al cargar perfil");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                showError(t.getMessage());
            }
        });
    }

    private void fetchInterests(User user) {
        catalogApi.getTravelInterests().enqueue(new Callback<InterestResponse>() {
            @Override
            public void onResponse(Call<InterestResponse> call, Response<InterestResponse> response) {
                showLoading(false);
                displayUser(user);
                if (response.isSuccessful() && response.body() != null) {
                    displayInterests(response.body().getItems(), user.getPreferences());
                }
            }
            @Override
            public void onFailure(Call<InterestResponse> call, Throwable t) {
                showLoading(false);
                displayUser(user);
            }
        });
    }

    private void updateProfile(String name, String email, String phone, List<String> interests) {
        showLoading(true);
        User updatedUser = new User(name, email, phone);
        updatedUser.setPreferences(new UserPreferences(interests));

        authApi.updateUserProfile(updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), getString(R.string.msg_save_success), Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    showError("Error al actualizar perfil");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                showError(t.getMessage());
            }
        });
    }

    private void displayUser(User user) {
        binding.editName.setText(user.getName());
        binding.editEmail.setText(user.getEmail());
        binding.editPhone.setText(user.getPhone());
    }

    private void showLoading(boolean isLoading) {
        binding.progressProfile.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSaveProfile.setEnabled(!isLoading);
    }

    private void showError(String error) {
        Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
    }

    private void displayInterests(List<Interest> catalog, UserPreferences preferences) {
        binding.interestsChipGroup.removeAllViews();
        List<String> userInterests = (preferences != null && preferences.getTravelInterests() != null) 
                ? preferences.getTravelInterests() : new ArrayList<>();

        for (Interest interest : catalog) {
            Chip chip = new Chip(getContext());
            chip.setText(interest.getLabel());
            chip.setTag(interest.getKey());
            chip.setCheckable(true);
            chip.setChecked(userInterests.contains(interest.getKey()));
            binding.interestsChipGroup.addView(chip);
        }
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.btnMyBookings.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_MyBookingsFragment));
        binding.btnBookingHistory.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_BookingHistoryFragment));
        binding.btnLogout.setOnClickListener(v -> {
            tokenManager.clearToken();
            Navigation.findNavController(v).navigate(R.id.action_ProfileFragment_to_AuthFragment);
        });

        binding.btnSaveProfile.setOnClickListener(v -> {
            String name = binding.editName.getText().toString();
            String email = binding.editEmail.getText().toString();
            String phone = binding.editPhone.getText().toString();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.error_required_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> selectedInterests = new ArrayList<>();
            for (int i = 0; i < binding.interestsChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.interestsChipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedInterests.add((String) chip.getTag());
                }
            }
            updateProfile(name, email, phone, selectedInterests);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
