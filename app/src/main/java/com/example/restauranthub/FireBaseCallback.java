package com.example.restauranthub;

import com.google.firebase.firestore.QuerySnapshot;

public interface FireBaseCallback {
    void onCallback(QuerySnapshot result);
    void onFailure(Exception e);
}
