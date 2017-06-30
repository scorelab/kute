package com.scorelab.kute.kute.PrivateVehicles.App.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.scorelab.kute.kute.PrivateVehicles.App.Adapters.MyRoutesRecyclerAdapter;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

/**
 * Created by nipunarora on 06/06/17.
 */

public class MyRoutesTab extends Fragment {
    private final String TAG = "MyRoutesTab";
    ArrayList<Route> my_routes_list;
    RecyclerView my_routes_recycler;
    View v;

    public MyRoutesTab() {
    }

    /******************** Overrides *****/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        my_routes_list = new ArrayList<Route>();
        /***************** Creating test data ***********/
        for (int i = 0; i < 4; ++i) {
            Route temp = new Route("IndianRoute", "Delhi", "Punjab", 4, true);
            my_routes_list.add(temp);
        }
        /**************** Test data creation over *********/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.myroutes_tab_bottomnavigation, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        my_routes_recycler = (RecyclerView) v.findViewById(R.id.routeRecycler);
        MyRoutesRecyclerAdapter recycler_adapter = new MyRoutesRecyclerAdapter(my_routes_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        my_routes_recycler.setLayoutManager(mLayoutManager);
        my_routes_recycler.setItemAnimator(new DefaultItemAnimator());
        my_routes_recycler.setAdapter(recycler_adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }
    /********** End of Overrides ******/
}
