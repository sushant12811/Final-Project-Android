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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.restauranthub.localNotification.RestaurantBroadcastReceiver;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;


public class RestaurantRecyclerListingActivity extends AppCompatActivity implements ListenerInterface {

    private SearchView searchView;
    RecyclerView recyclerView;
    RestaurantListAdapter restaurantListAdapter;
    ArrayList<RestaurantModel> restaurantArrayList = new ArrayList<>();
    private ImageView favoriteIcon;
    FirebaseFirestore fbStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_recycler_listing);
        initializer();


        boolean isFavoritesListing = false;
        restaurantListAdapter = new RestaurantListAdapter(this, restaurantArrayList, this, isFavoritesListing);

        recyclerView = findViewById(R.id.restaurantRecycler);
        recyclerView.setAdapter(restaurantListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantCardDisplayed();
        searchQuery();

        favoriteIcon.setOnClickListener(v -> favIconTapped());

        fbStore = FirebaseFirestore.getInstance();

        notificationChannel();
        scheduleDailyNotification();

    }

    //Favourite Icon tapped to navigate favList
    private void favIconTapped() {
        Intent intent = new Intent(RestaurantRecyclerListingActivity.this, RestaurantFavouriteListing.class);
        startActivity(intent);


    }


    //initializer
    private void initializer(){
        searchView = findViewById(R.id.search_bar);
        favoriteIcon = findViewById(R.id.favourite_icon);

        }


        //Searching list
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
                    ArrayList<RestaurantModel> filteredList = new ArrayList<>();
                    for (RestaurantModel item : restaurantArrayList) {
                        if (item.getRestaurantName().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    if (filteredList.isEmpty()) {
                        Toast.makeText(RestaurantRecyclerListingActivity.this, "Sorry, No result", Toast.LENGTH_SHORT).show();
                    } else {
                        restaurantListAdapter.setFiltered(filteredList);
                    }
                }
            });
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        searchView.clearFocus();

                    }
                }
            });

            findViewById(R.id.restaurantRecycler_main).setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    searchView.clearFocus();
                    return false;
                }

            });


        }

        //Search view focus
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






            //Array list of recyclerView
    @SuppressLint("NotifyDataSetChanged")
    public void restaurantCardDisplayed(){
        String[] restaurantNames = getResources().getStringArray(R.array.restaurantNameArray);
        String[] locations = getResources().getStringArray(R.array.locationArray);
        String[] restaurantDetails = getResources().getStringArray(R.array.detailsArray);
        int[] resImagesId = {R.drawable.thefarmhouse, R.drawable.casamia, R.drawable.grillicious,
                R.drawable.baton, R.drawable.tajbistro, R.drawable.iibuco,R.drawable.zios,
                R.drawable.cottage, R.drawable.thecandadian, R.drawable.symposium, R.drawable.bull, R.drawable.choupaya};

          for(int i =0; i < restaurantNames.length; i++){
           restaurantArrayList.add(new RestaurantModel(
            restaurantNames[i],
            locations[i],
            null,
            restaurantDetails[i],
                   null,
                   resImagesId[i],
                  false

    ));


}
        restaurantListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(int pos) {
        Intent intent = new Intent(RestaurantRecyclerListingActivity.this, RestaurantDetailsActivity.class);
        RestaurantModel restaurantModel = restaurantArrayList.get(pos);
        intent.putExtra("resInfo", restaurantModel);
        startActivity(intent);

    }

    @Override
    public void onRemove(int pos) {

    }

//Notification Channel
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


    // Set the time for the notification
    public void scheduleDailyNotification() {
        Intent intent = new Intent(this, RestaurantBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 8);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
