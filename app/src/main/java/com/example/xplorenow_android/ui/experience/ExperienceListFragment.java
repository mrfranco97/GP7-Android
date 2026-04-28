package com.example.xplorenow_android.ui.experience;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.paging.LoadState;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.local.BookingDao;
import com.example.xplorenow_android.data.model.Booking;
import com.example.xplorenow_android.data.model.Experience;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.BookingCancellationResponse;
import com.example.xplorenow_android.data.network.ExperienceApi;
import com.example.xplorenow_android.data.network.ExperienceResponse;
import com.example.xplorenow_android.data.network.MyBookingsResponse;
import com.example.xplorenow_android.databinding.FragmentExperienceListBinding;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ExperienceListFragment extends Fragment implements FilterBottomSheetFragment.FilterListener {

    private FragmentExperienceListBinding binding;
    private ExperienceAdapter adapter;
    private RecommendedAdapter recommendedAdapter;
    private ExperienceFilters filters = new ExperienceFilters();
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String PREFS_NAME = "profile_prefs";
    private static final String KEY_IMAGE_URI = "image_uri";

    @Inject
    ExperienceApi experienceApi;

    @Inject
    BookingApi bookingApi;

    @Inject
    BookingDao bookingDao;

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
        setupFilters();
        setupProfileNavigation();
        setupNetworkMonitoring();
        
        loadExperiences();
        preFetchBookings();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchRecommendations();
        loadProfileImage();
    }

    private void loadProfileImage() {
        android.content.SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String uriString = prefs.getString(KEY_IMAGE_URI, null);
        if (uriString != null) {
            Glide.with(this)
                    .load(android.net.Uri.parse(uriString))
                    .circleCrop()
                    .into(binding.imageProfileAvatar);
        } else {
            binding.imageProfileAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void setupNetworkMonitoring() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                if (isAdded()) {
                    syncOfflineChanges();
                }
            }
        });
    }

    private void syncOfflineChanges() {
        executor.execute(() -> {
            List<Booking> pending = bookingDao.getPendingCancellations();
            for (Booking booking : pending) {
                bookingApi.cancelBooking(booking.getId()).enqueue(new Callback<BookingCancellationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BookingCancellationResponse> call, @NonNull Response<BookingCancellationResponse> response) {
                        if (response.isSuccessful()) {
                            executor.execute(() -> {
                                booking.setPendingCancellation(false);
                                bookingDao.insertBooking(booking);
                            });
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<BookingCancellationResponse> call, @NonNull Throwable t) {}
                });
            }
        });
    }

    private void preFetchBookings() {
        bookingApi.getMyBookings().enqueue(new Callback<MyBookingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyBookingsResponse> call, @NonNull Response<MyBookingsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> bookings = response.body().getItems();
                    executor.execute(() -> {
                        bookingDao.syncBookings(bookings);
                        syncOfflineChanges(); // Also try syncing after update
                    });
                }
            }
            @Override
            public void onFailure(@NonNull Call<MyBookingsResponse> call, @NonNull Throwable t) {
                syncOfflineChanges(); // Try syncing even if pre-fetch fails
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ExperienceAdapter(this::navigateToDetail);
        binding.recyclerExperiences.setAdapter(adapter);

        adapter.addLoadStateListener(loadStates -> {
            boolean isEmpty = loadStates.getRefresh() instanceof LoadState.NotLoading 
                    && adapter.getItemCount() == 0;
            
            binding.layoutNoResults.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            binding.recyclerExperiences.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            return null;
        });
    }

    private void loadExperiences() {
        Pager<Integer, Experience> pager = new Pager<>(
                new PagingConfig(10, 5, false),
                () -> new ExperiencePagingSource(experienceApi, filters)
        );
        LiveData<PagingData<Experience>> pagingDataLiveData = PagingLiveData.getLiveData(pager);
        
        pagingDataLiveData.observe(getViewLifecycleOwner(), pagingData -> {
            adapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData);
        });
    }

    private void fetchRecommendations() {
        experienceApi.getRecommendedExperiences().enqueue(new Callback<ExperienceResponse>() {
            @Override
            public void onResponse(Call<ExperienceResponse> call, Response<ExperienceResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    displayRecommendations(response.body().getItems());
                }
            }
            @Override
            public void onFailure(Call<ExperienceResponse> call, Throwable t) {}
        });
    }

    private void displayRecommendations(java.util.List<Experience> items) {
        if (items != null && !items.isEmpty()) {
            binding.sectionRecommended.setVisibility(View.VISIBLE);
            recommendedAdapter.setItems(items);
        } else {
            binding.sectionRecommended.setVisibility(View.GONE);
        }
    }

    private void setupRecommendedCarousel() {
        recommendedAdapter = new RecommendedAdapter(this::navigateToDetail);
        binding.recyclerRecommended.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerRecommended.setAdapter(recommendedAdapter);
    }

    private void navigateToDetail(Experience experience) {
        Bundle args = new Bundle();
        args.putInt("experienceId", experience.getId());
        Navigation.findNavController(requireView()).navigate(
                R.id.action_ExperienceListFragment_to_ExperienceDetailFragment, args);
    }

    private void setupFilters() {
        binding.btnShowFilters.setOnClickListener(v -> {
            FilterBottomSheetFragment bottomSheet = new FilterBottomSheetFragment();
            bottomSheet.setInitialFilters(filters);
            bottomSheet.setListener(this);
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
        });
    }

    @Override
    public void onFiltersApplied(ExperienceFilters nextFilters) {
        this.filters = nextFilters;
        loadExperiences();
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
