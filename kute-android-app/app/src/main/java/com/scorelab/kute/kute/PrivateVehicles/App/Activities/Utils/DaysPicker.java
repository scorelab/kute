package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.scorelab.kute.kute.PrivateVehicles.App.Adapters.DaysPickerAdapter;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.DaysPickerRecyclerActivityInterface;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

/**
 * Created by nipunarora on 28/07/17.
 */

public class DaysPicker extends AppCompatActivity implements DaysPickerRecyclerActivityInterface,View.OnClickListener {

    //Override functions
    ArrayList<Boolean>days=new ArrayList<Boolean>(); //A serialized format of passing the information of days to firebase by creating a sort of enum for the days of the week
    DaysPickerAdapter recycler_adapter;
    ImageButton backnav;
    private final int result_code=0x2;
    RecyclerView days_recycler;
    private final String  TAG="DaysPicker";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.days_picker_recycler);
        days=(ArrayList<Boolean>) getIntent().getSerializableExtra("DaysList");
        backnav=(ImageButton)findViewById(R.id.backNav);
        backnav.setOnClickListener(this);
        days_recycler=(RecyclerView)findViewById(R.id.daysSelectorRecycler);
        recycler_adapter=new DaysPickerAdapter(this,days);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        days_recycler.setLayoutManager(mLayoutManager);
        days_recycler.setItemAnimator(new DefaultItemAnimator());
        days_recycler.setAdapter(recycler_adapter);
        if(days==null) {
            days=new ArrayList<Boolean>();
            for (int i = 0; i < 7; ++i) {
                days.add(i, false);
            }
        }

        
    }

    @Override
    public void getDay(String action,int day_num) {
        Log.i(TAG,"getDay:Received Message");
        switch(action){
            case "Delete":
                days.set(day_num,false);
                break;
            case "Add":
                days.set(day_num,true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backNav:
                Intent i=new Intent();
                i.putExtra("DayList",days);
                setResult(result_code,i);
                finish();
                break;
        }
    }
}
