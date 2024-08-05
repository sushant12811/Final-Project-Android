package com.example.restauranthub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.restauranthub.localNotification.RestaurantBroadcastReceiver;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class RestaurantRecyclerListingActivity extends AppCompatActivity implements ListenerInterface {

    private SearchView searchView;
    RecyclerView recyclerView;
    RestaurantListAdapter restaurantListAdapter;
    ArrayList<RestaurantModel> restaurantArrayList = new ArrayList<>();
    ArrayList<RestaurantModel> filteredList = new ArrayList<>();
    private ImageView favoriteIcon;
    FirebaseFirestore fbStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_recycler_listing);
        initializer();

        fbStore = FirebaseFirestore.getInstance();
        FireBaseConfig fireBaseConfig = new FireBaseConfig(fbStore);
        fireBaseConfig.getRestaurants(new FireBaseCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCallback(QuerySnapshot result) {
                if (result != null) {
                    restaurantArrayList.clear(); // Clear previous data if any
                    for (QueryDocumentSnapshot document : result) {
                        Log.d("TAG ", document.getId() + " => " + document.getData());
                        restaurantArrayList.add(new RestaurantModel(
                                document.getString("restaurantNames"),
                                document.getString("restaurantLocation"),
                                document.getString("restaurantImage"),
                                document.getString("restaurantDetails"),
                                document.getString("restaurantRating"),
                                0,
                                false
                        ));
                    }
                    filteredList.addAll(restaurantArrayList);
                    restaurantListAdapter.notifyDataSetChanged();
                    Log.d("TAG ", "Restaurant List: " + restaurantArrayList);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("TAG ", "Error getting documents.", e);
            }
        });

        boolean isFavoritesListing = false;
        restaurantListAdapter = new RestaurantListAdapter(this, filteredList, this, isFavoritesListing);

        recyclerView = findViewById(R.id.restaurantRecycler);
        recyclerView.setAdapter(restaurantListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchQuery();
        favoriteIcon.setOnClickListener(v -> favIconTapped());

        notificationChannel();
        scheduleDailyNotification();
    }

    private void favIconTapped() {
        Intent intent = new Intent(RestaurantRecyclerListingActivity.this, RestaurantFavouriteListing.class);
        startActivity(intent);
    }

    private void initializer() {
        searchView = findViewById(R.id.search_bar);
        favoriteIcon = findViewById(R.id.favourite_icon);
    }

    private void searchQuery() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }

            private void filterList(String newText) {
                filteredList.clear();
                for (RestaurantModel item : restaurantArrayList) {
                    if (item.getRestaurantName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                if (filteredList.isEmpty()) {
                    Toast.makeText(RestaurantRecyclerListingActivity.this, "Sorry, No result", Toast.LENGTH_SHORT).show();
                }
                restaurantListAdapter.notifyDataSetChanged();
            }
        });
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchView.clearFocus();
            }
        });

        findViewById(R.id.restaurantRecycler_main).setOnTouchListener((v, event) -> {
            searchView.clearFocus();
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView.clearFocus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchView.clearFocus();
    }


    @Override
    public void onClick(int pos) {
        Intent intent = new Intent(RestaurantRecyclerListingActivity.this, RestaurantDetailsActivity.class);
        RestaurantModel restaurantModel = filteredList.get(pos);
        intent.putExtra("resInfo", restaurantModel);
        startActivity(intent);
    }

    @Override
    public void onRemove(int pos) {
        // Implement if needed
    }

    private void notificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Daily Notification";
            String description = "Channel for daily restaurant notifications";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("dailyRestaurantChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleDailyNotification() {
        Intent intent = new Intent(this, RestaurantBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1);

        // Set the time to 8 AM
//        calendar.set(Calendar.HOUR_OF_DAY, 9);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);


        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
