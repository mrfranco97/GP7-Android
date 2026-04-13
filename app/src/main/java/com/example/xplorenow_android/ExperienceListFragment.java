package com.example.xplorenow_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.xplorenow_android.databinding.FragmentExperienceListBinding;

public class ExperienceListFragment extends Fragment {

    private FragmentExperienceListBinding binding;
    private ExperienceAdapter adapter;
    private ExperienceViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentExperienceListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupViewModel();
        setupFilters();
        setupProfileNavigation();
    }

    private void setupRecyclerView() {
        adapter = new ExperienceAdapter();
        binding.recyclerExperiences.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ExperienceViewModel.class);
        
        viewModel.getPagingDataLiveData().observe(getViewLifecycleOwner(), pagingData -> {
            adapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData);
        });
    }

    private void setupFilters() {
        View.OnClickListener filterListener = v -> {
            if (v instanceof Button) {
                String category = ((Button) v).getText().toString();
                viewModel.setCategory(category);
                binding.recyclerExperiences.scrollToPosition(0);
            }
        };

        binding.btnFilterAll.setOnClickListener(filterListener);
        binding.btnFilterNature.setOnClickListener(filterListener);
        binding.btnFilterCulture.setOnClickListener(filterListener);
        binding.btnFilterGastronomy.setOnClickListener(filterListener);
        binding.btnFilterAdventure.setOnClickListener(filterListener);
    }

    private void setupProfileNavigation() {
        binding.imageProfileAvatar.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_ExperienceListFragment_to_ProfileFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
