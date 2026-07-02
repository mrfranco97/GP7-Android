package com.example.xplorenow_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.local.AppDatabase;
import com.example.xplorenow_android.databinding.ActivityMainBinding;
import com.example.xplorenow_android.di.AuthEventBus;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    AuthEventBus authEventBus;

    @Inject
    AppDatabase db;

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.ExperienceListFragment ||
                    destination.getId() == R.id.ProfileFragment ||
                    destination.getId() == R.id.FavoritesFragment) {
                binding.bottomNav.setVisibility(View.VISIBLE);
            } else {
                binding.bottomNav.setVisibility(View.GONE);
            }
        });

        authEventBus.getSessionExpired().observe(this, expired -> {
            if (Boolean.TRUE.equals(expired)) {
                navController.navigate(
                        R.id.AuthFragment,
                        null,
                        new NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build()
                );
            }
        });

        db.networkStatusDao().isOnline().observe(this, isOnline -> {
            if (isOnline != null) {
                binding.textNoConnection.setVisibility(isOnline ? View.GONE : View.VISIBLE);
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("experienceId")) {
            int experienceId = intent.getIntExtra("experienceId", -1);
            if (experienceId != -1) {
                Bundle args = new Bundle();
                args.putInt("experienceId", experienceId);
                navController.navigate(R.id.ExperienceDetailFragment, args);
                // Clear extra to avoid re-triggering on rotation
                intent.removeExtra("experienceId");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
