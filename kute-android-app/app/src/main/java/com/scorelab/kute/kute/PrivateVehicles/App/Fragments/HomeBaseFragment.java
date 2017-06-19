package com.scorelab.kute.kute.PrivateVehicles.App.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 18/06/17.
 */
//This Fragment serves as home fragment of the navigation drawer
public class HomeBaseFragment extends Fragment {
    View v;
    BottomNavigationView bottomNavigation;
    public void HomeBaseFragment()
    {}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.home_fragment,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bottomNavigation=(BottomNavigationView)v.findViewById(R.id.bottomnavigation);
        /*********** Bottom Navigation Setup *******/
        //set initial home fragment
        getChildFragmentManager().beginTransaction().replace(R.id.frameBottomBar,new HomeTab()).commit();
        //Add a bottom navigation click listener
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment x=null;
                switch(item.getItemId())
                {
                    case R.id.hometab:x=new HomeTab();
                        break;
                    case R.id.friendstab:x=new FriendTab();
                        break;
                    case R.id.myroutestab:x=new MyRoutesTab();
                        break;

                }
                getChildFragmentManager().beginTransaction().replace(R.id.frameBottomBar,x).commit();

                return true;
            }
        });
        /*********** End of bottom navigation *****/

    }
}
