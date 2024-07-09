package com.example.restauranthub.localNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.restauranthub.RestaurantRecyclerListingActivity;

public class RestaurantBootBroadcasterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            RestaurantRecyclerListingActivity activity = new RestaurantRecyclerListingActivity();
            activity.scheduleDailyNotification();
        }
    }
}
