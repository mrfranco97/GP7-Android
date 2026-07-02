package com.example.xplorenow_android.data.network;

import com.example.xplorenow_android.data.model.NotificationItem;
import java.util.List;

public class NotificationsResponse {
    private List<NotificationItem> notifications;

    public List<NotificationItem> getNotifications() {
        return notifications;
    }
}
