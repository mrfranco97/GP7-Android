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

import android.widget.Toast;

@AndroidEntryPoint
public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private FavoritesAdapter adapter;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchFavorites();
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
        if (favorite.getActivity() != null) {
            favoriteApi.removeFavorite(favorite.getActivity().getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), R.string.msg_favorite_removed, Toast.LENGTH_SHORT).show();
                        fetchFavorites();
                    } else {
                        Toast.makeText(getContext(), "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateToDetail(Favorite favorite) {
        if (favorite.getNovelty() != null && favorite.getNovelty().hasNews()) {
            favoriteApi.markFavoriteSeen(favorite.getActivity().getId()).enqueue(new Callback<Favorite>() {
                @Override
                public void onResponse(@NonNull Call<Favorite> call, @NonNull Response<Favorite> response) {}
                @Override
                public void onFailure(@NonNull Call<Favorite> call, @NonNull Throwable t) {}
            });
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