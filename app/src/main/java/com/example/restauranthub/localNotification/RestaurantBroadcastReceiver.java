package com.example.restauranthub.localNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.restauranthub.R;


public class RestaurantBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Check if the app has the necessary permission to post notifications
        if (checkNotificationPermission(context)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "dailyRestaurantChannel")
                    .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                    .setContentTitle("Today's Best Restaurants")
                    .setContentText("Check out the best restaurants of the day!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(100, builder.build());
        } else {
            Log.e("NotificationReceiver", "Permission not granted to post notifications");
        }
    }


    // Method to check if the app has notification permissions
    private boolean checkNotificationPermission(Context context) {
        return PackageManager.PERMISSION_GRANTED ==
                context.checkSelfPermission(android.Manifest.permission.RECEIVE_BOOT_COMPLETED);
    }
}
