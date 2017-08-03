package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Main;
import com.scorelab.kute.kute.PrivateVehicles.App.Adapters.MyRoutesRecyclerAdapter;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.R;
import java.util.ArrayList;


/**
 * Created by nipunarora on 30/07/17.
 */

public class RouteListPerson extends AppCompatActivity implements View.OnClickListener {
    RecyclerView my_routes_recycler;
    FloatingActionButton home;
    ImageButton back_nav;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_list);
        my_routes_recycler = (RecyclerView) findViewById(R.id.routeRecycler);
        MyRoutesRecyclerAdapter recycler_adapter = new MyRoutesRecyclerAdapter((ArrayList<Route>) getIntent().getSerializableExtra("RouteList"),(String)getIntent().getStringExtra("Name"));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        my_routes_recycler.setLayoutManager(mLayoutManager);
        my_routes_recycler.setItemAnimator(new DefaultItemAnimator());
        my_routes_recycler.setAdapter(recycler_adapter);
        back_nav=(ImageButton)findViewById(R.id.backNav);
        back_nav.setOnClickListener(this);
        home=(FloatingActionButton)findViewById(R.id.fab);
        home.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                Intent i=new Intent(getApplicationContext(), Main.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case R.id.backNav:
                finish();
                break;
        }
    }
}
