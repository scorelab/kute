package com.scorelab.kute.kute.PrivateVehicles.App.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scorelab.kute.kute.PrivateVehicles.App.Activities.CurrentFriendList;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;


/**
 * Created by nipunarora on 18/06/17.
 */

public class FriendTab extends Fragment implements View.OnClickListener {
    private final String TAG="FriendTab";
    View v;
    ArrayList<Person> friend_detail_list;
    ArrayList<String>friendslist;
    AppCompatButton viewall_currentfriends;
    public FriendTab() {
        Log.d(TAG,"New Fragment Created");
        friend_detail_list=new ArrayList<Person>();
        friendslist=new ArrayList<String>();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onViewCreated");
        v=inflater.inflate(R.layout.friend_tab_bottomnavigation,container,false);
        viewall_currentfriends=(AppCompatButton)v.findViewById(R.id.viewallCurrentfriends);
        viewall_currentfriends.setEnabled(false); //The button wont be active until and unless we get the entire friendlist
        viewall_currentfriends.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onACtivityCreated");
        getChildFragmentManager().beginTransaction().replace(R.id.friendRequestsFrame,new FriendFrame()).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.currentFriendsFrame,new PlaceHolderFragment()).commit();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.viewallCurrentfriends:Intent i =new Intent(getContext(), CurrentFriendList.class);
                i.putExtra("FriendList",friendslist);
                startActivity(i);
             //TODO handle the viewall for friend requests as well
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }
    /********************* Custom Functions *************/
    //Receive Message  from Activity and other fragments
    public void receiveMessage(String Action,Object...attachment )
    {
        Log.d(TAG,"Message Received : "+Action);
        switch (Action)
        {
            case "Friends_Ready":
                friendslist=(ArrayList<String>)attachment[0];
                friend_detail_list=(ArrayList<Person>)attachment[1];
                if(friendslist.size()>0) {
                    setupCurrentFriends();
                }
        }
    }

    //Load the fragment for current Friends single Friend Frame
    public void setupCurrentFriends()
    {
        Log.d(TAG,"Setting Up Current Friend");
        FriendFrame f=new FriendFrame();
        Bundle args=new Bundle();
        args.putSerializable("Friend_1",friend_detail_list.get(0));
        f.setArguments(args);
        viewall_currentfriends.setEnabled(true);
        getChildFragmentManager().beginTransaction().replace(R.id.currentFriendsFrame,f).commit();
    }
}
