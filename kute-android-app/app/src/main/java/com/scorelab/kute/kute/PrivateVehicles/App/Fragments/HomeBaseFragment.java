package com.scorelab.kute.kute.PrivateVehicles.App.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.scorelab.kute.kute.PrivateVehicles.App.AsyncTasks.LoadFirebaseFriends;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;
import com.scorelab.kute.kute.PrivateVehicles.App.Services.SyncFacebookFriendsToFirebase;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;
import java.util.Objects;


/**
 * Created by nipunarora on 18/06/17.
 */
//This Fragment serves as home fragment of the navigation drawer
public class HomeBaseFragment extends Fragment implements AsyncTaskListener{
    private final String TAG = "HomeBaseFragment";
    View v;
    BottomNavigationView bottomNavigation;
    FragmentManager fm;
    HomeTab ht;
    FriendTab ft;
    MyRoutesTab mrt;
    ArrayList<String> friend_list;
    SharedPreferences.OnSharedPreferenceChangeListener pref_change_listener;
    SharedPreferences prefs;
    Boolean is_receiver_register = false;
    BroadcastReceiver sync_friend_service_receiver;
    IntentFilter filter_sync_friend_receiver;
    private final String Action = SyncFacebookFriendsToFirebase.class.getName() + "Complete";
    Boolean is_asynctask_running=false;
    LoadFirebaseFriends load_friends_async;

    /***************************** Default Constructor ****************/
    public HomeBaseFragment() {
        Log.d(TAG, "Constructor");
        ht = new HomeTab();
        ft = new FriendTab();
        mrt = new MyRoutesTab();
    }

    /***************************** Overrides ****************************/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);    //Handling Screen Orientation changes can also be done by locking the screen orientation
        //We need to handle these orientation changes as we execute an asynctask here which can cause a Memory Leak with orientation changes
        //Orientation Change may reinstantiate fragment causing another asynctask to be executed also if asynctask references views it can lead to a serious memory leak
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_fragment, container, false);
        fm = getChildFragmentManager();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bottomNavigation = (BottomNavigationView) v.findViewById(R.id.bottomnavigation);
        //Initialising Broadcast receiver with its intent filter
        sync_friend_service_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Returned From SyncFacebookFriendsToFirebase Service");
                getFirebaseFriend();

            }
        };
        filter_sync_friend_receiver = new IntentFilter(Action);
        /*********** Bottom Navigation Setup *******/
        //set initial fragments We have loaded all the three fragments simultaneously
        // inorder to smooth out the transition between the three fragments
        FragmentTransaction frag_transaction = fm.beginTransaction();
        frag_transaction.add(R.id.frameBottomBar, ht, "HomeTab");
        frag_transaction.add(R.id.frameBottomBar, ft, "FriendTab");
        frag_transaction.add(R.id.frameBottomBar, mrt, "MyRoutesTab");
        frag_transaction.hide(ft);
        frag_transaction.hide(mrt);
        frag_transaction.commit();
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Here We are just creating a single instance of a fragment
                // and storing it in the Fragment backstack so that it we can invoke methods implemented through the Interfaces
                //For activity Communication to the fragment
                switch (item.getItemId()) {
                    case R.id.hometab:
                        showHomeTab();
                        break;
                    case R.id.friendstab:
                        showFriendTab();
                        break;
                    case R.id.myroutestab:
                        showMyRoutesTab();
                        break;
                }
                return true;
            }
        });
        /*********** End of bottom navigation *****/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //get Friend list from Firebase
        prefs = getActivity().getSharedPreferences("user_credentials", 0);
        if (prefs.getBoolean("Sync_Friends_db", true)) {
            Log.d(TAG,"Sync Friends Receiver Registered");
            is_receiver_register = true;
            getActivity().registerReceiver(sync_friend_service_receiver, filter_sync_friend_receiver);
        } else {
            getFirebaseFriend();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (is_receiver_register) {
            getActivity().unregisterReceiver(sync_friend_service_receiver);
        }
    }

    @Override
    public void onTaskStarted(Object... attachments) {
    }

    @Override
    public void onTaskCompleted(Object attachment) {
        sendMessageFriendTab("Friends_Ready",friend_list,(ArrayList<Person>) attachment);
    }

    /******************** End of Overrides **********/

    /******************************** Custom Function ***************************/
    //Load friends from firebase
    public void getFirebaseFriend() {
        /******************** Getting Friends From Firebase *************/
        String ref = "Friends/" + getActivity().getSharedPreferences("user_credentials", 0).getString("Name", null);
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
                getFirstFriendProfile();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    //Get the entire profile of first Friend
    private void getFirstFriendProfile()
    {
        DatabaseReference users=FirebaseDatabase.getInstance().getReference("Users");
        final ArrayList<Person>person_detail_list=new ArrayList<Person>();
        if(friend_list.size()>0) {
            String first_friend_key = friend_list.get(0);
            Query get_top_friend=users.orderByKey().equalTo(first_friend_key);
            get_top_friend.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Person p=dataSnapshot.getChildren().iterator().next().getValue(Person.class);
                    Log.d(TAG,"First Friend:"+p.name);
                    person_detail_list.add(p);
                    Log.d(TAG,"Starting to load friends via firebase");
                    load_friends_async=new LoadFirebaseFriends(friend_list,HomeBaseFragment.this);
                    load_friends_async.execute(1,9);
                    sendMessageFriendTab("Friends_Ready",friend_list,person_detail_list);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


    }
    //Send messages to friend tab
    private void sendMessageFriendTab(String Message,ArrayList<String>friend_list1,ArrayList<Person>person_detail_list) {
        try {
            FriendTab fragment =
                    (FriendTab) getChildFragmentManager().findFragmentByTag("FriendTab");
            if (fragment != null) {
                if (fragment.getView() != null) {
                    switch (Message) {
                        case "Friends_Ready":
                            fragment.receiveMessage("Friends_Ready", friend_list1,person_detail_list);
                    }
                    Log.d(TAG, "Message Sent to Friend Tab");
                } else {
                    Log.d(TAG, "sendMessage:Fragment View was not found");
                }
            }
        } catch (RuntimeException e) {
            Log.d(TAG, "SendMessage : Exception ->" + e.toString());
            e.printStackTrace();
        }
    }

    /************************ FUnctions to toggle fragment visibility ***************/
    private void showHomeTab() {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (ht.isAdded()) {
            fragmentTransaction.show(ht);
            Log.d(TAG, "Fragment transaction: HomeTab -> Found Previous One");
        } else {
            fragmentTransaction.add(R.id.frameBottomBar, ht, "HomeTab");
        }
        //Hide The other two fragments
        if (ft.isAdded()) {
            fragmentTransaction.hide(ft);
        }
        if (mrt.isAdded()) {
            fragmentTransaction.hide(mrt);
        }
        fragmentTransaction.commit();
    }

    private void showFriendTab() {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (ft.isAdded()) {
            fragmentTransaction.show(ft);
        } else {
            fragmentTransaction.add(R.id.frameBottomBar, ft, "FriendTab");
        }
        //Hide The other two fragments
        if (ft.isAdded()) {
            fragmentTransaction.hide(ht);
        }
        if (mrt.isAdded()) {
            fragmentTransaction.hide(mrt);
        }
        fragmentTransaction.commit();
    }

    private void showMyRoutesTab() {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (mrt.isAdded()) {
            fragmentTransaction.show(mrt);
        } else {
            fragmentTransaction.add(R.id.frameBottomBar, mrt, "MyRoutesTab");
        }
        //Hide The other two fragments
        if (ft.isAdded()) {
            fragmentTransaction.hide(ft);
        }
        if (mrt.isAdded()) {
            fragmentTransaction.hide(ht);
        }
        fragmentTransaction.commit();
    }
}

