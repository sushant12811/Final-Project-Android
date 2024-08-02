package com.example.restauranthub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FireBaseConfig fireBaseConfig = new FireBaseConfig(db);
        Log.i("Database", fireBaseConfig.toString());
        setContentView(R.layout.activity_main);
    }
}