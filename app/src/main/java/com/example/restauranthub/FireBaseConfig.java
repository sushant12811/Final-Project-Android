package com.example.restauranthub;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FireBaseConfig {
    private FirebaseFirestore db;

    public FireBaseConfig(FirebaseFirestore db) {
        this.db = db;
    }

    public void getRestaurants(final FireBaseCallback callback) {
        db.collection("restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            callback.onCallback(task.getResult());
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }
}