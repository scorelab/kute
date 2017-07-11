package com.scorelab.kute.kute.PrivateVehicles.App.AsyncTasks;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by nipunarora on 10/07/17.
 */

public class LoadFirebaseFriends extends AsyncTask<Integer,Void,Void> {
    ArrayList<String>friend_list;
    WeakReference<AsyncTaskListener> async_task_listener;
    String TAG="LoadFirebaseFriend";

    public LoadFirebaseFriends(ArrayList<String>friend_list, AsyncTaskListener asyncTaskListener) {
        this.friend_list=friend_list;
        this.async_task_listener=new WeakReference<AsyncTaskListener>(asyncTaskListener);

    }

    @Override
    protected Void doInBackground(Integer... params) {
        //Through params we can pass the indices for which data is required
        Log.v(TAG,"The indices received here are "+Integer.toString(params[0])+"and "+Integer.toString(params[1]));
        final ArrayList<Person>person_detail_list=new ArrayList<Person>();
        DatabaseReference users= FirebaseDatabase.getInstance().getReference("Users");
        final AsyncTaskListener taskListener=async_task_listener.get();
        taskListener.onTaskStarted(params[0],params[1]);
        for(int i=params[0];i<=params[1];++i) {
            Log.v(TAG,"The size in asynctask is"+Integer.toString(friend_list.size()));
            if (i<friend_list.size()) {
                Log.v(TAG,"Status:entered");
                String first_friend_key = friend_list.get(i);
                Query get_friend = users.orderByKey().equalTo(first_friend_key);
                get_friend.keepSynced(true);
                get_friend.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Person p = dataSnapshot.getChildren().iterator().next().getValue(Person.class);
                        if(async_task_listener!=null){
                            //Check if the weak reference still exists
                            Log.v(TAG,"Retrieved Friend "+p.name);
                            taskListener.onTaskCompleted(p);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            else {
                Log.v(TAG,"All Friends Already Downloaded");
                break;
            }
        }
        return null;
    }

}
