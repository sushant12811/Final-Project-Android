package com.example.restauranthub.widgetService;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.restauranthub.R;
import com.example.restauranthub.RestaurantModel;

import java.util.ArrayList;
import java.util.List;

public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final List<RestaurantModel> widgetRestaurants = new ArrayList<>();

    public WidgetViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        String[] restaurantNames = context.getResources().getStringArray(R.array.restaurantNameArray);
        int[] resImagesId = {R.drawable.thefarmhouse, R.drawable.casamia, R.drawable.grillicious,
                R.drawable.baton, R.drawable.tajbistro, R.drawable.iibuco, R.drawable.zios};

        for (int i = 0; i < resImagesId.length; i++) {
            widgetRestaurants.add(new RestaurantModel(
                    restaurantNames[i],
                    null,
                    null,
                    null,
                    null,
                    resImagesId[i],
                    false
            ));
        }
    }

    @Override
    public void onDataSetChanged() {
        // Update data if necessary
    }

    @Override
    public void onDestroy() {
        widgetRestaurants.clear();
    }

    @Override
    public int getCount() {
        return widgetRestaurants.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget_item);
        RestaurantModel restaurant = widgetRestaurants.get(position);

        views.setTextViewText(R.id.app_widget_item_text, restaurant.getRestaurantName());
        views.setImageViewResource(R.id.app_widget_item_image, restaurant.getRestaurantImageResourceId());

        Intent fillInIntent = new Intent();
        views.setOnClickFillInIntent(R.id.app_widget_item, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
