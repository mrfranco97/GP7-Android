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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.model.Favorite;
import com.example.xplorenow_android.data.network.FavoriteApi;
import com.example.xplorenow_android.data.network.FavoriteResponse;
import com.example.xplorenow_android.databinding.FragmentFavoritesBinding;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private FavoritesAdapter adapter;
    private SharedFavoriteViewModel sharedViewModel;

    @Inject
    FavoriteApi favoriteApi;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        fetchFavorites();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedFavoriteViewModel.class);
        sharedViewModel.getFavoriteUpdate().observe(getViewLifecycleOwner(), update -> {
            if (!update.isFavorite()) {
                fetchFavorites(); // Refrescar la lista si se eliminó de favoritos
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new FavoritesAdapter(new FavoritesAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(Favorite favorite) {
                navigateToDetail(favorite);
            }

            @Override
            public void onRemoveFavorite(Favorite favorite) {
                removeFromFavorites(favorite);
            }
        });
        binding.recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerFavorites.setAdapter(adapter);
    }

    private void fetchFavorites() {
        showLoading(true);
        favoriteApi.getFavorites().enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteResponse> call, @NonNull Response<FavoriteResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    displayFavorites(response.body().getItems());
                } else {
                    showEmptyState(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteResponse> call, @NonNull Throwable t) {
                showLoading(false);
                showEmptyState(true);
            }
        });
    }

    private void displayFavorites(List<Favorite> items) {
        if (items == null || items.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            adapter.setItems(items);
        }
    }

    private void removeFromFavorites(Favorite favorite) {
        if (sharedViewModel != null && favorite.getActivity() != null) {
            // Llamamos al viewModel con el ID de la experiencia
            sharedViewModel.toggleFavorite(favorite.getActivity().getId(), true);
        }
    }

    private void navigateToDetail(Favorite favorite) {
        if (sharedViewModel != null && favorite.getNovelty() != null && favorite.getNovelty().hasNews()) {
            sharedViewModel.markFavoriteSeen(favorite.getActivity().getId());
        }

        Bundle args = new Bundle();
        args.putInt("experienceId", favorite.getActivity().getId());
        Navigation.findNavController(requireView()).navigate(
                R.id.action_FavoritesFragment_to_ExperienceDetailFragment, args);
    }

    private void showLoading(boolean isLoading) {
        binding.progressFavorites.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showEmptyState(boolean isEmpty) {
        binding.layoutEmptyFavorites.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerFavorites.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}