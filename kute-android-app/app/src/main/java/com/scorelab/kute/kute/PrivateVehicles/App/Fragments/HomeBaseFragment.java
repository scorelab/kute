package com.scorelab.kute.kute.PrivateVehicles.App.Fragments;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.scorelab.kute.kute.PrivateVehicles.App.AsyncTasks.LoadFirebaseFriends;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.HomeScreenTabFragments.FriendTab;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.HomeScreenTabFragments.HomeTab;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.HomeScreenTabFragments.MyRoutesTab;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;
import com.scorelab.kute.kute.PrivateVehicles.App.Miscelleneous.FabMenu;
import com.scorelab.kute.kute.PrivateVehicles.App.Services.SyncFacebookFriendsToFirebase;
import com.scorelab.kute.kute.R;

import java.lang.annotation.Target;
import java.util.ArrayList;


/**
 * Created by nipunarora on 18/06/17.
 */
//This Fragment serves as home fragment of the navigation drawer
public class HomeBaseFragment extends Fragment implements AsyncTaskListener,View.OnClickListener{
    private final String TAG = "HomeBaseFragment";
    View v;
    BottomNavigationView bottomNavigation;
    FragmentManager fm;
    HomeTab home_tab;
    FriendTab friend_tab;
    MyRoutesTab my_routes_tab;
    ArrayList<String> friend_list;
    ArrayList<Person>person_detail_list;
    SharedPreferences.OnSharedPreferenceChangeListener pref_change_listener;
    SharedPreferences prefs;
    Boolean is_receiver_register = false;
    BroadcastReceiver sync_friend_service_receiver;
    IntentFilter filter_sync_friend_receiver;
    private final String Action = SyncFacebookFriendsToFirebase.class.getName() + "Complete";
    final String Action_FRIENDS_READY="Friends_Ready";
    final String Action_FRIENDS_ADDED="Added_Detail_Friends";
    int start_index_async,last_index_async;
    boolean is_async_task_running=false;// A boolean created to prevent a new asynctask being created everytime we scroll down
    LoadFirebaseFriends load_friends_async;



    /***************************** Default Constructor ****************/
    public HomeBaseFragment() {
        Log.d(TAG, "Constructor");
        home_tab = new HomeTab();
        friend_tab = new FriendTab();
        my_routes_tab = new MyRoutesTab();
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
        person_detail_list=new ArrayList<Person>();
        friend_list=new ArrayList<String>();



        /*********** Bottom Navigation Setup *******/
        //set initial fragments We have loaded all the three fragments simultaneously
        // inorder to smooth out the transition between the three fragments
        FragmentTransaction frag_transaction = fm.beginTransaction();
        frag_transaction.add(R.id.frameBottomBar, home_tab, "HomeTab");
        frag_transaction.add(R.id.frameBottomBar, friend_tab, "FriendTab");
        frag_transaction.add(R.id.frameBottomBar, my_routes_tab, "MyRoutesTab");
        frag_transaction.hide(friend_tab);
        frag_transaction.hide(my_routes_tab);
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
        ImageButton edit_icon=(ImageButton)getActivity().findViewById(R.id.routeRequests);//The Id is search icon because this image button is used for search in the homebase fragment
        edit_icon.setOnClickListener(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (is_receiver_register) {
            getActivity().unregisterReceiver(sync_friend_service_receiver);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.routeRequests:
                break;
        }
    }

    //Asynctask Interacting interface
    @Override
    public void onTaskStarted(Object...attachments) {
        //toggle the boolean to show that asynctask is running
        is_async_task_running=true;
        //get the index from asynctask
        start_index_async=(int)attachments[0];
        last_index_async=(int)attachments[1];
    }

    @Override
    public void onTaskCompleted(Object attachment) {
        Person temp=(Person)attachment;
        Log.d(TAG,"Person Received : "+temp.name);
        person_detail_list.add(temp);
        Log.d(TAG,"Received in test Friend name: "+temp.name);
        if(start_index_async>=last_index_async) {
            is_async_task_running=false;
            //send Message to friend Tab
            sendMessageFriendTab(Action_FRIENDS_READY,friend_list,person_detail_list);
        }
        ++start_index_async;

    }

    /******************** End of Overrides **********/

    /******************************** Custom Function ***************************/

    /********************************* Data Querying Methods *************************/
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
        if(friend_list.size()>0) {
            String first_friend_key = friend_list.get(0);
            Query get_top_friend=users.orderByKey().equalTo(first_friend_key);
            get_top_friend.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Person p=dataSnapshot.getChildren().iterator().next().getValue(Person.class);
                    Log.d(TAG,"First Friend:"+p.name);
                    person_detail_list.add(p);
                    sendMessageFriendTab(Action_FRIENDS_READY,friend_list,person_detail_list);
                    //Now Loading First Ten friends //TODO cancel the asynctask on fragment's onDestroy
                    Log.i(TAG,"Starting to load top 10 friends");
                    load_friends_async=new LoadFirebaseFriends(friend_list,HomeBaseFragment.this);
                    load_friends_async.execute(2,9); //Just Loading details of some random number of people in order
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


    }
    /*********************** Functions Communicating with child fragments *************/
    //Send messages to friend tab
    private void sendMessageFriendTab(String Message,ArrayList<String>friend_list1,ArrayList<Person>person_detail_list) {
        try {
            FriendTab fragment =
                    (FriendTab) getChildFragmentManager().findFragmentByTag("FriendTab"); //Finding fragment from the manager
            if (fragment != null) {
                if (fragment.getView() != null) {
                    switch (Message) {
                        case Action_FRIENDS_READY:
                            fragment.receiveMessage(Action_FRIENDS_READY, friend_list1,person_detail_list);
                            break;
                        case Action_FRIENDS_ADDED:
                            fragment.receiveMessage(Action_FRIENDS_ADDED, friend_list1,person_detail_list);
                            break;

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
        if (home_tab.isAdded()) {
            fragmentTransaction.show(home_tab);
            Log.d(TAG, "Fragment transaction: HomeTab -> Found Previous One");
        } else {
            fragmentTransaction.add(R.id.frameBottomBar, home_tab, "HomeTab");
        }
        //Hide The other two fragments
        if (friend_tab.isAdded()) {
            fragmentTransaction.hide(friend_tab);
        }
        if (my_routes_tab.isAdded()) {
            fragmentTransaction.hide(my_routes_tab);
        }
        fragmentTransaction.commit();
    }

    private void showFriendTab() {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (friend_tab.isAdded()) {
            fragmentTransaction.show(friend_tab);
        } else {
            fragmentTransaction.add(R.id.frameBottomBar, friend_tab, "FriendTab");
        }
        //Hide The other two fragments
        if (friend_tab.isAdded()) {
            fragmentTransaction.hide(home_tab);
        }
        if (my_routes_tab.isAdded()) {
            fragmentTransaction.hide(my_routes_tab);
        }
        fragmentTransaction.commit();
    }

    private void showMyRoutesTab() {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (my_routes_tab.isAdded()) {
            fragmentTransaction.show(my_routes_tab);
        } else {
            fragmentTransaction.add(R.id.frameBottomBar, my_routes_tab, "MyRoutesTab");
        }
        //Hide The other two fragments
        if (friend_tab.isAdded()) {
            fragmentTransaction.hide(friend_tab);
        }
        if (my_routes_tab.isAdded()) {
            fragmentTransaction.hide(home_tab);
        }
        fragmentTransaction.commit();
    }

}


