package com.example.xplorenow_android.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.local.AppDatabase;
import com.example.xplorenow_android.data.local.TokenManager;
import com.example.xplorenow_android.data.model.NotificationItem;
import com.example.xplorenow_android.data.network.NotificationsApi;
import com.example.xplorenow_android.databinding.ActivityMainBinding;
import com.example.xplorenow_android.di.AuthEventBus;
import com.example.xplorenow_android.service.PollingService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Inject
    AuthEventBus authEventBus;

    @Inject
    AppDatabase db;

    @Inject
    TokenManager tokenManager;

    @Inject
    NotificationsApi notificationsApi;

    private ActivityMainBinding binding;
    private NavController navController;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted && tokenManager.hasToken()) {
                    startPollingService();
                }
            });

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
                stopPollingService();
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

        // Verificar permisos e iniciar servicio
        checkNotificationPermission();

        handleNotificationIntent(getIntent());
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                if (tokenManager.hasToken()) startPollingService();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            if (tokenManager.hasToken()) startPollingService();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent != null) {
            String notificationId = intent.getStringExtra("notification_id");
            if (notificationId != null) {
                markNotificationAsRead(notificationId);
            }

            if (intent.hasExtra("booking_id")) {
                String bookingId = intent.getStringExtra("booking_id");
                String voucherUrl = intent.getStringExtra("voucher_url");
                
                Bundle args = new Bundle();
                args.putString("bookingId", bookingId);
                if (voucherUrl != null) {
                    args.putString("voucherUrl", voucherUrl);
                }
                
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

                // Si el usuario está logueado, nos aseguramos de que el Home sea la base del historial
                // reemplazando el AuthFragment si este existiera.
                if (tokenManager.hasToken()) {
                    navController.navigate(R.id.ExperienceListFragment, null,
                            new NavOptions.Builder()
                                    .setPopUpTo(R.id.AuthFragment, true)
                                    .setLaunchSingleTop(true)
                                    .build());
                }

                navController.navigate(R.id.BookingDetailFragment, args);
            }
        }
    }

    private void markNotificationAsRead(String notificationId) {
        notificationsApi.markAsRead(notificationId).enqueue(new Callback<NotificationItem>() {
            @Override
            public void onResponse(Call<NotificationItem> call, Response<NotificationItem> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Notificación marcada como leída: " + notificationId);
                }
            }

            @Override
            public void onFailure(Call<NotificationItem> call, Throwable t) {
                Log.e(TAG, "Error al marcar notificación como leída", t);
            }
        });
    }

    public void startPollingService() {
        Intent intent = new Intent(this, PollingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public void stopPollingService() {
        Intent intent = new Intent(this, PollingService.class);
        stopService(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
