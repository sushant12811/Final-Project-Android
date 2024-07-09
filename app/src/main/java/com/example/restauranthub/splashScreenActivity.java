package com.example.restauranthub;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

@SuppressLint("CustomSplashScreen")
public class splashScreenActivity extends AppCompatActivity {

     ImageView splashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashImage = findViewById(R.id.splashImageId);


        animateImageView(splashImage);


        new Handler().postDelayed(() -> {

            Intent mainIntent = new Intent(splashScreenActivity.this, RestaurantRecyclerListingActivity.class);
            startActivity(mainIntent);
            finish();
        }, 2500);
    }
    private void animateImageView(ImageView splashImage) {
        splashImage.setScaleX(0f);
        splashImage.setScaleY(0f);
        splashImage.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1500)
                .start();
    }
}

