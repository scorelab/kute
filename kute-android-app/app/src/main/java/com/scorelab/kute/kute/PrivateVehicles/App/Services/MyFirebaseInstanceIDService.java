package com.scorelab.kute.kute.PrivateVehicles.App.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by nipunarora on 21/08/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendTokenToFirebase(refreshedToken);
    }

    private void sendTokenToFirebase(String refreshed_token){
        String person_id=getSharedPreferences("user_credentials", 0).getString("Id", null);
        if(person_id!=null){
            //When the person is already registered on firebase
            DatabaseReference user_ref= FirebaseDatabase.getInstance().getReference("Users/"+person_id);
            user_ref.child("token").setValue(refreshed_token).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"Error updating the refreshed database token "+e.toString());
                }
            });

        }

    }
}
