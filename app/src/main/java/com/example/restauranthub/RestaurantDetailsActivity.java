package com.example.restauranthub;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.JoiningType.TRANSPARENT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class RestaurantDetailsActivity extends AppCompatActivity {

    TextView detailsDisplay, restaurantNameD, locationD;
    String maroon = "#9E3434";
    int maroonColor = Color.parseColor(maroon);
    FirebaseFirestore fbStore;
    FirebaseStorage fbStorage;
    StorageReference storageRef;
    boolean isFavorite = false;
    ImageView detailsImageDisplay, backButtonD, favouriteIconD, gMapIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        initializer();

        fbStore = FirebaseFirestore.getInstance();

        //Scrolling the details view

        detailsDisplay.setMovementMethod(new ScrollingMovementMethod());

        RestaurantModel resModel = (RestaurantModel) getIntent().getSerializableExtra("resInfo");

        if (resModel != null) {
            restaurantNameD.setText(resModel.getRestaurantName());
            locationD.setText(resModel.getRestaurantLocation());
            detailsDisplay.setText(resModel.getRestaurantDetails());
            if (resModel.getRestaurantImage() != null && !resModel.getRestaurantImage().isEmpty()) {
                Glide.with(this)
                        .load(resModel.getRestaurantImage())
                        .into(detailsImageDisplay);
            } else if (resModel.getRestaurantImageResourceId() != 0) {
                Glide.with(this)
                        .load(resModel.getRestaurantImageResourceId())
                        .into(detailsImageDisplay);

            favIconRetrieved();
        }

    }


        favouriteIconD.setEnabled(false);
        favIconRetrieved();
        setClickListener();

    }



    //initializer
    private void initializer(){
        detailsDisplay = findViewById(R.id.textView);
        restaurantNameD = findViewById(R.id.restaurantName);
        locationD = findViewById(R.id.location);
        detailsImageDisplay = findViewById(R.id.detailsImage);
        backButtonD = findViewById(R.id.backButton);
        favouriteIconD = findViewById(R.id.favourite_icon);
        gMapIcon = findViewById(R.id.mapIcon);

    }

    ///clickListener
    private void setClickListener(){
        backButtonD.setOnClickListener(v -> backPressed());
        gMapIcon.setOnClickListener(v -> showMap());
        favouriteIconD.setOnClickListener(v -> favIconTapped());


    }


    //Fav Icon Tapped and save to Firebase
    private void favIconTapped() {
        if (isFavorite) {
            favouriteIconD.setColorFilter(TRANSPARENT);
            removeRestaurantFromFavorites();


        } else {
            favouriteIconD.setColorFilter(maroonColor);
            addRestaurantToFavorites();
        }
    }


    private void addRestaurantToFavorites() {
        String restaurantName = restaurantNameD.getText().toString().trim();
        String restaurantLocation = locationD.getText().toString().trim();
        String restaurantDetails = detailsDisplay.getText().toString().trim();

        detailsImageDisplay.setDrawingCacheEnabled(true);
        detailsImageDisplay.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) detailsImageDisplay.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Log.w(TAG, "Image upload failed", exception);
            Toast.makeText(RestaurantDetailsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            isFavorite = false;
            favouriteIconD.setColorFilter(Color.TRANSPARENT);
        }).addOnSuccessListener(taskSnapshot -> {
            imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Prepare restaurant data for FireStore
                Map<String, Object> restaurantData = new HashMap<>();
                restaurantData.put("restaurantName", restaurantName);
                restaurantData.put("restaurantLocation", restaurantLocation);
                restaurantData.put("restaurantDetails", restaurantDetails);
                restaurantData.put("isFavorite", true);
                restaurantData.put("restaurantImage", imageUrl);

                // Save restaurant data to FireStore
                fbStore.collection("users").document(restaurantName)
                        .set(restaurantData)
                        .addOnSuccessListener(aVoid -> {
                            isFavorite = true;
                            Toast.makeText(RestaurantDetailsActivity.this, "Saved to Favourites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(RestaurantDetailsActivity.this, "Failed to add to favourites", Toast.LENGTH_SHORT).show();
                            // Revert favorite status if Firestore operation fails
                            isFavorite = false;
                            favouriteIconD.setColorFilter(Color.TRANSPARENT);
                        });
            });
        });
    }

    private void removeRestaurantFromFavorites() {
        String restaurantName = restaurantNameD.getText().toString().trim();

        fbStore.collection("users").document(restaurantName)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    isFavorite = false;
                    Toast.makeText(RestaurantDetailsActivity.this, "Removed from Favourites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e);
                    Toast.makeText(RestaurantDetailsActivity.this, "Failed to remove from favourites", Toast.LENGTH_SHORT).show();
                    // Revert favorite status if Firestore operation fails
                    isFavorite = true;
                    favouriteIconD.setColorFilter(maroonColor);
                });
    }


    private void favIconRetrieved() {
        String currentRestaurantName = restaurantNameD.getText().toString().trim();
        fbStore.collection("users").document(currentRestaurantName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    isFavorite = Boolean.TRUE.equals(document.getBoolean("isFavorite"));
                    if (isFavorite) {
                        favouriteIconD.setColorFilter(maroonColor);
                    } else {
                        favouriteIconD.setColorFilter(Color.GRAY);
                    }
                } else {
                    isFavorite = false;
                    favouriteIconD.setColorFilter(Color.GRAY);
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
            favouriteIconD.setEnabled(true);
        });

    }

    //Navigate to mapView
    private void showMap() {
        Intent intent = new Intent(RestaurantDetailsActivity.this, mapView.class);
        startActivity(intent);
        finish();

    }


//navigate to previous screen
    private  void backPressed(){
   finish();

}

}