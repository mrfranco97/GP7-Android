package com.example.xplorenow_android.ui.experience;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.databinding.FragmentExperienceListBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ExperienceListFragment extends Fragment {

    private FragmentExperienceListBinding binding;
    private ExperienceAdapter adapter;
    private RecommendedAdapter recommendedAdapter;
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
        setupRecommendedCarousel();
        setupViewModel();
        setupFilters();
        setupProfileNavigation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.fetchRecommendations();
        }
    }

    private void setupRecyclerView() {
        adapter = new ExperienceAdapter();
        binding.recyclerExperiences.setAdapter(adapter);

        adapter.addLoadStateListener(loadStates -> {
            boolean isEmpty = loadStates.getRefresh() instanceof LoadState.NotLoading 
                    && adapter.getItemCount() == 0;
            
            binding.layoutNoResults.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            binding.recyclerExperiences.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            return null;
        });
    }

    private void setupRecommendedCarousel() {
        recommendedAdapter = new RecommendedAdapter();
        binding.recyclerRecommended.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerRecommended.setAdapter(recommendedAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ExperienceViewModel.class);
        
        viewModel.getPagingDataLiveData().observe(getViewLifecycleOwner(), pagingData -> {
            adapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData);
        });

        viewModel.getRecommendedLiveData().observe(getViewLifecycleOwner(), items -> {
            if (items != null && !items.isEmpty()) {
                binding.sectionRecommended.setVisibility(View.VISIBLE);
                recommendedAdapter.setItems(items);
            } else {
                binding.sectionRecommended.setVisibility(View.GONE);
            }
        });
    }

    private void setupFilters() {
        binding.btnShowFilters.setOnClickListener(v -> {
            FilterBottomSheetFragment bottomSheet = new FilterBottomSheetFragment();
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
        });
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
