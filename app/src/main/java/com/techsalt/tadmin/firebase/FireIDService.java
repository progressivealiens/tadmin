package com.techsalt.tadmin.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

public class FireIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e("Refreshed token: ", token);
    }
}
