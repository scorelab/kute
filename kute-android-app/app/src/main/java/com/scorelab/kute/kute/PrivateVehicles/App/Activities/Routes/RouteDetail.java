package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by nipunarora on 30/07/17.
 */

public class RouteDetail extends AppCompatActivity implements View.OnClickListener{
    TextView route_name,from,to,no_seats;
    TextView time;
    ImageButton back_nav;
    private final int color=Color.parseColor("#00BCD4");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail);
        back_nav=(ImageButton)findViewById(R.id.backNav);
        back_nav.setOnClickListener(this);
        setupDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.requestSeat:
                //Implement the logic for requesting the seat
                break;
            case R.id.backNav:
                finish();
                break;
        }
    }

    /******************* Custom Functions **********************/
    //A method call to setup details to views
    private void setupDetails(){
        Route route=(Route)getIntent().getSerializableExtra("Route");
        //Initialise the views
        route_name=(TextView)findViewById(R.id.routeName);
        from=(TextView)findViewById(R.id.startPlace);
        to=(TextView)findViewById(R.id.destination);
        time=(TextView)findViewById(R.id.startTime);
        no_seats=(TextView)findViewById(R.id.noSeats);

        //Connect with data model
        route_name.setText(route.getName());
        from.setText(route.getSource());
        to.setText(route.getDestination());
        no_seats.setText(route.getSeats_available().toString());
        time.setText(route.getTime());
        //Call method to get the details of the days
        if(route.getDays()!=null) {
            setupDays(route.getDays());
        }
    }

    private void setupDays(ArrayList<Boolean>days){
        for (int i=0;i<days.size();++i){
            if(days.get(i)){
                switch (i){
                    case 0:
                        TextView mon=(TextView)findViewById(R.id.dayMonday);
                        mon.setBackgroundColor(color);
                        break;
                    case 1:
                        TextView tues=(TextView)findViewById(R.id.dayTuesday);
                        tues.setBackgroundColor(color);
                        break;
                    case 2:
                        TextView wed=(TextView)findViewById(R.id.dayWednesday);
                        wed.setBackgroundColor(color);
                        break;
                    case 3:
                        TextView thurs=(TextView)findViewById(R.id.dayThursday);
                        thurs.setBackgroundColor(color);
                        break;
                    case 4:
                        TextView fri=(TextView)findViewById(R.id.dayFriday);
                        fri.setBackgroundColor(color);
                        break;
                    case 5:
                        TextView sat=(TextView)findViewById(R.id.daySaturday);
                        sat.setBackgroundColor(color);
                        break;
                    case 6:
                        TextView sun=(TextView)findViewById(R.id.daySunday);
                        sun.setBackgroundColor(color);
                        break;
                }
            }
        }

    }



}
