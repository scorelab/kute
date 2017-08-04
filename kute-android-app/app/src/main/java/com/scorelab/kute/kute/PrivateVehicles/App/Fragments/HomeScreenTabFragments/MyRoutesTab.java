package com.scorelab.kute.kute.PrivateVehicles.App.Fragments.HomeScreenTabFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes.AddRouteActivity;
import com.scorelab.kute.kute.PrivateVehicles.App.Adapters.MyRoutesRecyclerAdapter;
import com.scorelab.kute.kute.PrivateVehicles.App.AsyncTasks.LoadPersonRoutesAsyncTask;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

/**
 * Created by nipunarora on 06/06/17.
 */

public class MyRoutesTab extends Fragment implements View.OnClickListener,AsyncTaskListener {
    private final String TAG = "MyRoutesTab";
    ArrayList<Route> my_routes_list;
    RecyclerView my_routes_recycler;
    View v;
    private final int add_route_activity_code=0x1;
    MyRoutesRecyclerAdapter recycler_adapter;
    FloatingActionButton fab_add;
    Boolean is_loading_user_routes=false;
    LoadPersonRoutesAsyncTask routes_async_load;
    ProgressBar pg;

    public MyRoutesTab() {
    }

    /******************** Overrides *****/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        my_routes_list = new ArrayList<Route>();
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
        fab_add=(FloatingActionButton)v.findViewById(R.id.fab);
        fab_add.setOnClickListener(this);
        pg=(ProgressBar)v.findViewById(R.id.progressBar);
        my_routes_recycler = (RecyclerView) v.findViewById(R.id.routeRecycler);
        recycler_adapter = new MyRoutesRecyclerAdapter(my_routes_list,"My");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        my_routes_recycler.setLayoutManager(mLayoutManager);
        my_routes_recycler.setItemAnimator(new DefaultItemAnimator());
        my_routes_recycler.setAdapter(recycler_adapter);
        routes_async_load=new LoadPersonRoutesAsyncTask(this);
        routes_async_load.execute(getActivity().getSharedPreferences("user_credentials", 0).getString("Name", null));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.fab:
                Intent i=new Intent(getContext(), AddRouteActivity.class);
                startActivityForResult(i,add_route_activity_code);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==add_route_activity_code){
            //get the newly added route from data intent and add it to the recycler
            Route r=(Route)data.getSerializableExtra("Route");
            if (r!=null){
                Log.i(TAG,"onActivityResult : Received Route from add route");
                my_routes_list.add(r);
                recycler_adapter.notifyItemInserted(recycler_adapter.getItemCount()+1);
            }

        }
    }
    @Override
    public void onTaskStarted(Object... attachments) {
        is_loading_user_routes=true;
        pg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskCompleted(Object attachment) {
        ArrayList<Route>my_routes_list1 = (ArrayList<Route>) attachment;
        for(Route r:my_routes_list1){
            my_routes_list.add(r);
        }
        is_loading_user_routes = false;
        if(my_routes_list.size()!=0){
            Log.i(TAG,"on Task Completed "+my_routes_list.get(0).getName());
        }
        recycler_adapter.notifyItemRangeInserted(recycler_adapter.getItemCount(),my_routes_list.size());
        pg.setVisibility(View.GONE);

    }
    /********** End of Overrides ******/
}
