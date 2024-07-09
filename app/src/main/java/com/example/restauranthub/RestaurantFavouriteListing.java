package com.example.restauranthub;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class RestaurantFavouriteListing extends AppCompatActivity implements ListenerInterface {
    RecyclerView recyclerView;
    RestaurantListAdapter favListAdapter;
    RestaurantModel restaurantModel;
    ArrayList<RestaurantModel> favoriteRestaurantList = new ArrayList<>();
    RestaurantListAdapter restaurantListAdapter;
    private ImageView backBtn;
    FirebaseFirestore fbStore;
    private SearchView searchView;
    String maroon = "#9E3434";
    int maroonColor = Color.parseColor(maroon);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurent_favourite_listing);
        initializer();
        clickListener();

        recyclerView = findViewById(R.id.restaurantFavouriteRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favListAdapter = new RestaurantListAdapter(this, favoriteRestaurantList, this, true);
        recyclerView.setAdapter(favListAdapter);



        fbStore = FirebaseFirestore.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchDataFromDatabase();
    }

    private void initializer() {
    searchView = findViewById(R.id.search_bar);
        backBtn = findViewById(R.id.backButton);
    }


    private void clickListener() {

        backBtn.setOnClickListener(v -> backBtnPressed());
    }

    private void backBtnPressed() {
        finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDataFromDatabase() {
        fbStore.collection("users")
                .orderBy("restaurantName", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                   favoriteRestaurantList.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        RestaurantModel model = documentSnapshot.toObject(RestaurantModel.class);
                        if (model != null) {
                            model.setId(documentSnapshot.getId());
                            Log.d(TAG, "Restaurant Location: " + model.getRestaurantLocation());

                            favoriteRestaurantList.add(model);
                            model.setFavorite(true);
                        }



                    }
                    favListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching movies", e);
                    Toast.makeText(RestaurantFavouriteListing.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                });
    }


    private void removeRestaurantFromFavorites(int position) {
        String restaurantName = favoriteRestaurantList.get(position).getRestaurantName();

        fbStore.collection("users").document(restaurantName)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    favoriteRestaurantList.remove(position);
                    favListAdapter.notifyItemRemoved(position);
                    Toast.makeText(RestaurantFavouriteListing.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error removing document", e);
                    Toast.makeText(RestaurantFavouriteListing.this, "Failed to remove from Favorites", Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public void onClick(int pos) {
        Intent intent = new Intent(RestaurantFavouriteListing.this, RestaurantDetailsActivity.class);
        RestaurantModel restaurantModel = favoriteRestaurantList.get(pos);
        intent.putExtra("resInfo", restaurantModel);
        startActivity(intent);

    }

    @Override
    public void onRemove(int pos) {
        removeRestaurantFromFavorites(pos);

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
                for (RestaurantModel item : favoriteRestaurantList) {
                    if (item.getRestaurantName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                if (filteredList.isEmpty()) {
                    Toast.makeText(RestaurantFavouriteListing.this, "Sorry, No result", Toast.LENGTH_SHORT).show();
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

}
