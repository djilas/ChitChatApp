package com.example.chitchatapplication.firebaseSevice;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


//This code have been taken from internet
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FMC Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        //TODO: Handle FCM Messages here.
        //If the application is in the foreground handle both data and notification
        //Also if you intendd on generating you own

        Log.d(TAG, "HERE IS REMOTE MESSAGE: " + remoteMessage.getNotification().getBody());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "HERE IS TOKEN:  " + token);
    }
}
