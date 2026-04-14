package com.example.xplorenow_android;

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

import com.example.xplorenow_android.databinding.FragmentProfileBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        observeViewModel();
        setupListeners();

        viewModel.fetchProfile();
    }

    private void observeViewModel() {
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.editName.setText(user.getName());
                binding.editEmail.setText(user.getEmail());
                binding.editPhone.setText(user.getPhone());
                
                if (viewModel.getInterestsCatalogLiveData().getValue() != null) {
                    displayInterests(viewModel.getInterestsCatalogLiveData().getValue(), user.getPreferences());
                }
            }
        });

        viewModel.getInterestsCatalogLiveData().observe(getViewLifecycleOwner(), interests -> {
            User user = viewModel.getUserLiveData().getValue();
            if (user != null) {
                displayInterests(interests, user.getPreferences());
            }
        });

        viewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressProfile.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSaveProfile.setEnabled(!isLoading);
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getUpdateSuccessLiveData().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                viewModel.resetUpdateSuccess();
                Navigation.findNavController(requireView()).navigateUp();
                Toast.makeText(getContext(), getString(R.string.msg_save_success), Toast.LENGTH_LONG).show();
            }
        });
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

            viewModel.updateProfile(name, email, phone, selectedInterests);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
