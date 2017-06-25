package com.scorelab.kute.kute.PrivateVehicles.App.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nipunarora on 23/06/17.
 */
/********************* Creating an intent service to do the intensive task of mapping current facebook friends to Firebase******/
public class SyncFacebookFriendsToFirebase extends IntentService {
   /*************** Class Variables ********/
   private final String Action=SyncFacebookFriendsToFirebase.class.getName()+"Complete";
    private final String TAG=SyncFacebookFriendsToFirebase.class.getName();


    public SyncFacebookFriendsToFirebase() {
        super(SyncFacebookFriendsToFirebase.class.getName());
        setIntentRedelivery(true);//Something akin to start_sticky for conventional services
    }
    /***************** Overrides *****************/
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG,"Intent Received Starting service");
        String jsonArray = intent.getStringExtra("FriendArray");
        JSONArray array=null;
        try {
            array = new JSONArray(jsonArray);
        } catch (JSONException e) {
            Log.d(TAG,e.toString());
        }
        syncFacebookFriends(array);

    }


    /************* Function to perform Graph Request *********/
    private void syncFacebookFriends(JSONArray friends_array)
    {
        ArrayList<Person> fbfriends=new ArrayList<Person>();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("user_credentials", 0);
        DatabaseReference root= FirebaseDatabase.getInstance().getReference();
        final DatabaseReference users=root.child("Users");
        DatabaseReference friends=root.child("Friends");
        Log.d(TAG,pref.getString("Name",null));
        final DatabaseReference myfriends=friends.child(pref.getString("Name",null));
        Person temp;
        try {
            Log.d(TAG, "outside loop");
            for (int i = 0; i < friends_array.length(); ++i) {
                Log.d(TAG, "inside loop");
                JSONObject p = (JSONObject) friends_array.get(i);
                String person_id = p.getString("id");
                String person_name = p.getString("name");
                temp = new Person(person_id, person_name);
                myfriends.child(temp.id).setValue(temp).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Firebase Self Add Error:" + e.toString());

                    }
                });
                Log.d(TAG, "Added Facebook Friend " + temp.name);
            }

        } catch (Exception e) {
            Log.d(TAG, "error syncing friends" + e.toString());
        }
        pref.edit().putBoolean("Sync_Friends_db",false).commit();
        Intent tellMainActivity=new Intent(Action);
        sendBroadcast(tellMainActivity);



    }
}
