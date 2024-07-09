package com.example.restauranthub.widgetService;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetServiceClass extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetViewsFactory(this.getApplicationContext());
    }
}
