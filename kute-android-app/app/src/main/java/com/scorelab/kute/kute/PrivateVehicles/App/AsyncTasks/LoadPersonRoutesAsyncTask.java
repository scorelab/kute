package com.scorelab.kute.kute.PrivateVehicles.App.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;

import java.util.ArrayList;

/**
 * Created by nipunarora on 29/07/17.
 */

public class LoadPersonRoutesAsyncTask extends AsyncTask<String,Void,ArrayList<Route>> {
    AsyncTaskListener messenger_to_activity;
    ArrayList<Route> routes_list;
    private final String TAG="LoadRoutesAsynctask";
    public LoadPersonRoutesAsyncTask(AsyncTaskListener listener) {
        messenger_to_activity=listener;
    }

    //Overrides

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        messenger_to_activity.onTaskStarted();
        routes_list=new ArrayList<Route>();
    }

    @Override
    protected ArrayList<Route> doInBackground(String... params) {
        //Return the retrieved list of person's  routes from firebase
        String id=params[0];
        String route_ref="Routes/"+id;
        DatabaseReference routes_ref= FirebaseDatabase.getInstance().getReference(route_ref);
        routes_ref.keepSynced(true);
        routes_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot routes:dataSnapshot.getChildren()){
                    Route temp=routes.getValue(Route.class);
                    Log.i(TAG,temp.getName());
                    routes_list.add(temp);
                }
                messenger_to_activity.onTaskCompleted(routes_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return null;
    }

}
