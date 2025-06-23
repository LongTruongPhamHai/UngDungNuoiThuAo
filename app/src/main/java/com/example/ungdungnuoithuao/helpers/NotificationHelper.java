package com.example.ungdungnuoithuao.helpers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

public class NotificationHelper {
    public static void toggleDoNotDisturb(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager.isNotificationPolicyAccessGranted()) {
            int currentMode = manager.getCurrentInterruptionFilter();
            if (currentMode == NotificationManager.INTERRUPTION_FILTER_ALL) {
                manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                Toast.makeText(context, "Đã bật Không Làm Phiền", Toast.LENGTH_SHORT).show();
            } else {
                manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                Toast.makeText(context, "Đã tắt Không Làm Phiền", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS); // Sử dụng Settings đúng
            context.startActivity(intent);
            Toast.makeText(context, "Vui lòng cấp quyền truy cập thông báo", Toast.LENGTH_SHORT).show();
        }
    }
}
