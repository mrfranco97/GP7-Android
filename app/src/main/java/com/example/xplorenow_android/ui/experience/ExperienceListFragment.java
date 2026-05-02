package com.example.xplorenow_android.ui.experience;
import android.content.Intent;
import com.example.xplorenow_android.ui.news.NewsActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.xplorenow_android.data.network.AuthApi;
import com.example.xplorenow_android.data.network.BookingApi;
import com.example.xplorenow_android.data.network.BookingCancellationResponse;
import com.example.xplorenow_android.data.network.ExperienceApi;
import com.example.xplorenow_android.data.network.ExperienceResponse;
import com.example.xplorenow_android.data.network.MyBookingsResponse;
import com.example.xplorenow_android.databinding.FragmentExperienceListBinding;
import com.example.xplorenow_android.data.model.Favorite;
import com.example.xplorenow_android.data.network.FavoriteApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ExperienceListFragment extends Fragment implements FilterBottomSheetFragment.FilterListener {

    private static final String TAG = "ExperienceListFragment";
    private FragmentExperienceListBinding binding;
    private ExperienceAdapter adapter;
    private RecommendedAdapter recommendedAdapter;
    private ExperienceFilters filters = new ExperienceFilters();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private boolean isFetchingRecommendations = false;
    
    @Inject
    FavoriteApi favoriteApi;

    @Inject
    ExperienceApi experienceApi;

    @Inject
    AuthApi authApi;

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
        setupNewsNavigation();
        setupNetworkMonitoring();
        
        loadExperiences();
        preFetchBookings();
    }
    private void setupNewsNavigation() {
        binding.btnNews.setOnClickListener(v ->  {
            Intent intent = new Intent(requireContext(), NewsActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.refresh();
        }
        fetchRecommendations();
        loadProfileImage();
    }

    private void loadProfileImage() {
        authApi.getProfilePicture().enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Response<okhttp3.ResponseBody> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] bytes = response.body().bytes();
                        Glide.with(ExperienceListFragment.this)
                                .load(bytes)
                                .circleCrop()
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .into(binding.imageProfileAvatar);
                    } catch (Exception e) {
                        binding.imageProfileAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } else {
                    binding.imageProfileAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }

            @Override
            public void onFailure(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Throwable t) {
                if (isAdded()) {
                    binding.imageProfileAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        });
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
        adapter = new ExperienceAdapter(new ExperienceAdapter.OnExperienceClickListener() {
            @Override
            public void onExperienceClick(Experience experience) {
                navigateToDetail(experience);
            }

            @Override
            public void onFavoriteClick(Experience experience) {
                toggleFavorite(experience);
            }
        });
        binding.recyclerExperiences.setAdapter(adapter);

        adapter.addLoadStateListener(loadStates -> {
            boolean isEmpty = loadStates.getRefresh() instanceof LoadState.NotLoading 
                    && adapter.getItemCount() == 0;
            
            binding.layoutNoResults.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            binding.recyclerExperiences.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            return null;
        });
    }

    private void toggleFavorite(Experience experience) {
        boolean currentStatus = experience.isFavorite();
        boolean newStatus = !currentStatus;
        
        // Cambio local inmediato (Optimistic UI)
        experience.setFavorite(newStatus);
        adapter.notifyDataSetChanged();

        if (currentStatus) {
            favoriteApi.removeFavorite(experience.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), R.string.msg_favorite_removed, Toast.LENGTH_SHORT).show();
                    } else {
                        // Revertir si falla
                        experience.setFavorite(currentStatus);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    experience.setFavorite(currentStatus);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            favoriteApi.addFavorite(experience.getId()).enqueue(new Callback<Favorite>() {
                @Override
                public void onResponse(@NonNull Call<Favorite> call, @NonNull Response<Favorite> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), R.string.msg_favorite_added, Toast.LENGTH_SHORT).show();
                    } else {
                        experience.setFavorite(currentStatus);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Error al añadir a favoritos", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Favorite> call, @NonNull Throwable t) {
                    experience.setFavorite(currentStatus);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        if (isFetchingRecommendations) return;

        isFetchingRecommendations = true;
        experienceApi.getRecommendedExperiences().enqueue(new Callback<ExperienceResponse>() {
            @Override
            public void onResponse(Call<ExperienceResponse> call, Response<ExperienceResponse> response) {
                isFetchingRecommendations = false;
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    displayRecommendations(response.body().getItems());
                }
            }
            @Override
            public void onFailure(Call<ExperienceResponse> call, Throwable t) {
                isFetchingRecommendations = false;
                Log.e(TAG, "Error fetching recommendations", t);
            }
        });
    }

    private void displayRecommendations(java.util.List<Experience> items) {
        if (items != null && !items.isEmpty()) {
            List<Experience> uniqueItems = new ArrayList<>();
            Set<String> seenKeys = new HashSet<>();

            for (Experience item : items) {
                if (item == null || item.getName() == null) continue;

                String key = (item.getName().trim() + "|" +
                             (item.getDestination() != null ? item.getDestination().trim() : ""))
                             .toLowerCase();

                if (seenKeys.add(key)) {
                    uniqueItems.add(item);
                }
            }

            Log.d(TAG, "Recommendations - API returned: " + items.size() + ", Unique items: " + uniqueItems.size());

            if (!uniqueItems.isEmpty()) {
                binding.sectionRecommended.setVisibility(View.VISIBLE);
                recommendedAdapter.setItems(uniqueItems);
            } else {
                binding.sectionRecommended.setVisibility(View.GONE);
            }
        } else {
            binding.sectionRecommended.setVisibility(View.GONE);
        }
    }

    private void setupRecommendedCarousel() {
        recommendedAdapter = new RecommendedAdapter(new RecommendedAdapter.OnRecommendedClickListener() {
            @Override
            public void onExperienceClick(Experience experience) {
                navigateToDetail(experience);
            }

            @Override
            public void onFavoriteClick(Experience experience) {
                toggleFavoriteFromRecommended(experience);
            }
        });
        binding.recyclerRecommended.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerRecommended.setAdapter(recommendedAdapter);
    }

    private void toggleFavoriteFromRecommended(Experience experience) {
        boolean currentStatus = experience.isFavorite();
        boolean newStatus = !currentStatus;

        // Cambio local inmediato
        experience.setFavorite(newStatus);
        recommendedAdapter.notifyDataSetChanged();

        if (currentStatus) {
            favoriteApi.removeFavorite(experience.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), R.string.msg_favorite_removed, Toast.LENGTH_SHORT).show();
                    } else {
                        experience.setFavorite(currentStatus);
                        recommendedAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    experience.setFavorite(currentStatus);
                    recommendedAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            favoriteApi.addFavorite(experience.getId()).enqueue(new Callback<Favorite>() {
                @Override
                public void onResponse(@NonNull Call<Favorite> call, @NonNull Response<Favorite> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), R.string.msg_favorite_added, Toast.LENGTH_SHORT).show();
                    } else {
                        experience.setFavorite(currentStatus);
                        recommendedAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Error al añadir a favoritos", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Favorite> call, @NonNull Throwable t) {
                    experience.setFavorite(currentStatus);
                    recommendedAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
