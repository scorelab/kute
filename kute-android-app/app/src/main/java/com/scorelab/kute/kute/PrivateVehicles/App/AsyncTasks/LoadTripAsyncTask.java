package com.scorelab.kute.kute.PrivateVehicles.App.AsyncTasks;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Trip;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nipunarora on 20/08/17.
 */

public class LoadTripAsyncTask extends AsyncTask<String,Void,Void> {
    AsyncTaskListener listener;
    private final String TAG="LoadTripTAG";
    //Constructor
    public LoadTripAsyncTask(AsyncTaskListener listener1) {
        this.listener=listener1;
    }

    /****************************** Overrides *********************/
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onTaskStarted(null);
    }

    @Override
    protected Void doInBackground(final String... params) {
        final HashMap<String,Object> attachments=new HashMap<String, Object>();
        DatabaseReference trip_ref= FirebaseDatabase.getInstance().getReference("Trips");
        trip_ref.child(params[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Trip person_trip=dataSnapshot.getValue(Trip.class);
                if (person_trip==null){
                    //The person has no trip currently
                    listener.onTaskCompleted(null);
                    Log.d(TAG,"NO trips found");
                }else {
                    Log.d(TAG,"Active Trip Found");
                    attachments.put("Trip", person_trip);
                    if (person_trip.getRoute_id() != null) {

                            //The case when the given person is not the owner of the trip he is currently into.
                            DatabaseReference route_ref = FirebaseDatabase.getInstance().getReference("Routes/" + person_trip.getOwner_string());
                            route_ref.child(person_trip.getRoute_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Route route = dataSnapshot.getValue(Route.class);
                                    attachments.put("Route", route);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                    }
                    //Retrieving all people travelling on that trip
                    final ArrayList<Person> people_travelling_with=new ArrayList<Person>();
                    final ArrayList<String> people_id=person_trip.getTravelling_with();
                    DatabaseReference user_ref=FirebaseDatabase.getInstance().getReference("Users");
                    for(int i=0;i<people_id.size();++i){
                        final int j=i;
                        user_ref.child(people_id.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                people_travelling_with.add(dataSnapshot.getValue(Person.class));
                                if(j==people_id.size()-1){
                                    attachments.put("People",people_travelling_with);
                                    listener.onTaskCompleted(attachments);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        trip_ref.child(params[0]).keepSynced(true);
        return null;
    }
}
