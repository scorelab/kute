package com.scorelab.kute.kute.PrivateVehicles.App.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scorelab.kute.kute.PrivateVehicles.App.Activities.CurrentFriendList;
import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 18/06/17.
 */

public class FriendTab extends Fragment implements View.OnClickListener {
    View v;
    public FriendTab() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.friend_tab_bottomnavigation,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.friendRequestsFrame,new FriendFrame()).commit();
        getFragmentManager().beginTransaction().replace(R.id.currentFriendsFrame,new FriendFrame()).commit();
        AppCompatButton viewall_currentfriends=(AppCompatButton)v.findViewById(R.id.viewallCurrentfriends);
        viewall_currentfriends.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.viewallCurrentfriends:Intent i =new Intent(getContext(), CurrentFriendList.class);
                startActivity(i);
             //TODO handle the viewall for friend requests as well
        }
    }
}
