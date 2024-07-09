package com.example.restauranthub.widgetService;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.restauranthub.R;
import com.example.restauranthub.RestaurantRecyclerListingActivity;

public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        // Set up the intent that starts the WidgetService, which will provide the views for this collection
        Intent intent = new Intent(context, WidgetServiceClass.class);
        views.setRemoteAdapter(R.id.app_widget_stack, intent);

        // Set up the intent template to handle clicks on individual items
        Intent appIntent = new Intent(context, RestaurantRecyclerListingActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(
                context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setPendingIntentTemplate(R.id.app_widget_stack, appPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
