package com.example.xplorenow_android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.xplorenow_android.R;
import com.example.xplorenow_android.data.local.TokenManager;
import com.example.xplorenow_android.data.model.NotificationItem;
import com.example.xplorenow_android.data.network.NotificationsApi;
import com.example.xplorenow_android.data.network.NotificationsResponse;
import com.example.xplorenow_android.ui.MainActivity;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Response;

@AndroidEntryPoint
public class PollingService extends Service {

    private static final String TAG = "PollingService";
    private static final String CHANNEL_ID = "polling_channel";
    private static final String NOTIF_CHANNEL_ID = "notifications_channel";
    private static final int FOREGROUND_ID = 1;

    @Inject
    NotificationsApi notificationsApi;

    @Inject
    TokenManager tokenManager;

    private Thread pollingThread;
    private volatile boolean running = true;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("XploreNow")
                .setContentText("Buscando novedades...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();

        startForeground(FOREGROUND_ID, notification);

        if (pollingThread == null || !pollingThread.isAlive()) {
            running = true;
            pollingThread = new Thread(this::pollLoop);
            pollingThread.start();
        }

        return START_STICKY;
    }

    private void pollLoop() {
        while (running) {
            if (!tokenManager.hasToken()) {
                stopSelf();
                break;
            }

            try {
                Response<NotificationsResponse> response = notificationsApi.pollNotifications().execute();
                
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationItem> notifications = response.body().getNotifications();
                    if (notifications != null) {
                        for (NotificationItem item : notifications) {
                            showSystemNotification(item);
                        }
                    }
                } else if (response.code() == 401) {
                    stopSelf();
                    break;
                } else {
                    Thread.sleep(5000);
                }
            } catch (IOException e) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {}
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    private void showSystemNotification(NotificationItem item) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notification_id", item.getId());
        if (item.getData() != null) {
            intent.putExtra("booking_id", String.valueOf(item.getData().getBookingId()));
            intent.putExtra("voucher_url", item.getData().getVoucherUrl());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 
                item.getId().hashCode(), 
                intent, 
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setContentTitle(item.getTitle())
                .setContentText(item.getBody())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        manager.notify(item.getId().hashCode(), notification);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager == null) return;

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Servicio de Sincronización",
                    NotificationManager.IMPORTANCE_LOW
            );
            manager.createNotificationChannel(channel);

            NotificationChannel alertChannel = new NotificationChannel(
                    NOTIF_CHANNEL_ID,
                    "Alertas de Viaje",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(alertChannel);
        }
    }

    @Override
    public void onDestroy() {
        running = false;
        if (pollingThread != null) pollingThread.interrupt();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
