package com.scorelab.kute.kute.PrivateVehicles.App.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scorelab.kute.kute.PrivateVehicles.App.AsyncTasks.LoadFirebaseFriends;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.HomeBaseFragment;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

public class test extends AppCompatActivity implements AsyncTaskListener {
    Button b;
    ArrayList<Person>list;
    LoadFirebaseFriends load_friends_async;
    ArrayList<String> friend_list;
    String TAG="Test";
    int start_index_async,last_index_async;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        b=(Button)findViewById(R.id.testButton);
        friend_list=new ArrayList<String>();
        list=new ArrayList<Person>();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //addTestFriends();
                getFirebaseFriend();
            }
        });
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void addTestFriends(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
        DatabaseReference friend_ref=ref.child("Friends/Nipun Arora");
        DatabaseReference user_ref=ref.child("Users");
        final String[]names={"Nipun","Harsh","Sukhad","Vishrut","jwalin","Shivam","Jatin","Archit","Shubham","Rajat"};
        list=new ArrayList<Person>();
        for (int i=0;i<10;++i)
        {
            String key=user_ref.push().getKey();
            final Person temp=new Person(key,names[i]);
            user_ref.child(key).setValue(temp).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("test","Failed as"+e.toString());
                }
            });
            friend_ref.child(key).setValue("true");
            Log.v("testit","Added "+names[i]);

        }
    }

    //Asynctask Interacting interface
    @Override
    public void onTaskStarted(Object...attachments) {
        //get the index from asynctask
        start_index_async=(int)attachments[0];
        last_index_async=(int)attachments[1];
    }

    @Override
    public void onTaskCompleted(Object attachment) {
            Log.d(TAG,"The start indice is "+Integer.toString(start_index_async));
            Person temp=(Person)attachment;
            list.add(temp);
            Log.d(TAG,"Received in test Friend name: "+temp.name);
        if(start_index_async>=last_index_async) {
            Log.d(TAG,"called");
            Intent i = new Intent(test.this, CurrentFriendList.class);
            i.putExtra("FriendList", friend_list);
            i.putExtra("FriendDetailList", list);
            startActivity(i);
        }
        ++start_index_async;

    }

    public void getFirebaseFriend() {
        /******************** Getting Friends From Firebase *************/
        String ref = "Friends/" +"Nipun Arora";
        Log.d(TAG, "Firebase Reference :" + ref);
        DatabaseReference friends = FirebaseDatabase.getInstance().getReference(ref);
        friends.keepSynced(true);
        friends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot k : dataSnapshot.getChildren()) {
                    friend_list.add(k.getKey());
                    Log.d(TAG, "Debug Firebase data query" + k.getValue().toString());
                }

                Log.d(TAG, String.format("The Friend List Size is %d",friend_list.size()));
                load_friends_async=new LoadFirebaseFriends(friend_list,test.this);
                load_friends_async.execute(0,8);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
