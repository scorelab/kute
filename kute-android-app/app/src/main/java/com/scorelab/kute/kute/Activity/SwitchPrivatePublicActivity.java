package com.scorelab.kute.kute.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.LandActivity;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.InitialDetailDialogs;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Main;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Services.SyncFacebookFriendsToFirebase;
import com.scorelab.kute.kute.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by nipunarora on 19/06/17.
 */

public class SwitchPrivatePublicActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton publicVeh, privateVeh;
    private final String TAG = "SwitchActivity";
    BroadcastReceiver sync_friend_service_receiver;
    IntentFilter filter_sync_friend_receiver;
    private final String Action = SyncFacebookFriendsToFirebase.class.getName() + "Complete";
    SharedPreferences pref;

    /*****************  Overrides ***************/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_public_private_activity_layout);
        /************* Initialise Views *************************/
        publicVeh = (ImageButton) findViewById(R.id.publicVehicle);
        privateVeh = (ImageButton) findViewById(R.id.privateVehicle);
        publicVeh.setOnClickListener(this);
        privateVeh.setOnClickListener(this);
        pref = getApplicationContext().getSharedPreferences("user_credentials", 0); // 0 - for private mode
        /****************** Sync Data For First Timers to the app *********/
        /******************* Register to Firebase for new User ********/
        if (pref.getBoolean("Register_db", true)) {
            registerFirebaseDbSelf(pref);
            pref.edit().putBoolean("Register_db", false).apply();
        }
        if (pref.getBoolean("Sync_Friends_db", true)) {
            syncFacebookFriendsDb();
            //Boolean will be inverted at the end of service
        }
        //Initialising Broadcast receiver with its intent filter
        sync_friend_service_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Returned From SyncFacebookFriendsToFirebase Service");
                //TODO alter the friend fragment to show up friends
                Boolean x = pref.getBoolean("Sync_Friends_db", true);
                Log.d(TAG, "onReceive Broadcast" + x.toString());
            }
        };
        filter_sync_friend_receiver = new IntentFilter(Action);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publicVehicle:
                Intent i = new Intent(this, LandActivity.class);
                startActivity(i);
                break;
            case R.id.privateVehicle:
                handlePrivateVehicleClick();
                break;
            default:
                Toast.makeText(this, "Not Clickable", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    /*************** End Of overrides ***********/
    /**************** Custom Functions Invoked only once on app installation *******/

    public void registerFirebaseDbSelf(final SharedPreferences pref) {
        Person temp = new Person(pref.getString("Name", null), pref.getString("Id", null), pref.getString("Profile_Image", null));
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = root.child("Users");
        Log.d(TAG, "Saving Self To db");
        users.child(temp.id).setValue(temp).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Firebase Self Add Error:" + e.toString());
                //reinvert the boolean if saving the user details face an error
                pref.edit().putBoolean("Register_db", true).apply();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(sync_friend_service_receiver, filter_sync_friend_receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(sync_friend_service_receiver);
    }

    /***************** Other Custom Functions ***********/
    private void syncFacebookFriendsDb() {
        try {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
            /* handle the result */
                            JSONObject x = response.getJSONObject();
                            Person temp;
                            try {
                                JSONArray dta = x.getJSONArray("data");
                                Log.d("friend json", dta.toString());
                                Intent start_friend_sync_service = new Intent(getApplicationContext(), SyncFacebookFriendsToFirebase.class);
                                start_friend_sync_service.putExtra("FriendArray", dta.toString());
                                startService(start_friend_sync_service);
                                Log.d(TAG, "Friend Sync Service Intent Sent");
                            } catch (Exception e) {
                                Log.d(TAG, "Graph Request Callback error" + e.toString());
                            }
                        }
                    }).executeAsync();
        } catch (Exception e) {
            Log.d(TAG, "Graph Request error" + e.toString());
        }
    }
    //Handles the click on the private vehicle part
    private void handlePrivateVehicleClick(){
        if(pref.getBoolean("update_profile_done",false)){
            Intent pvi = new Intent(this, Main.class);
            startActivity(pvi);
        }
        else {
            Intent pvi = new Intent(this, InitialDetailDialogs.class);
            startActivity(pvi);
        }
    }
}
