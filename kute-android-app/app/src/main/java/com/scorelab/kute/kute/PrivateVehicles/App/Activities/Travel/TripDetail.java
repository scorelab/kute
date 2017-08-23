package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.CurrentFriendList;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Trip;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

/**
 * Created by nipunarora on 21/08/17.
 */

public class TripDetail extends AppCompatActivity implements View.OnClickListener{
    //Members
    AppCompatTextView source_text,destination_text,time_text,host_name;
    TextView host_address_text;
    ImageButton back_nav,travelling_with,host_location_map;
    Button end_trip;
    TableRow host_name_row,host_address_row,travelling_with_row;
    Trip data;
    ArrayList<Person> travelling_with_people;
    private final int END_TRIP=111;
    private final String TAG="TripDetail";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Reference the views
        source_text=(AppCompatTextView)findViewById(R.id.startPlace);
        destination_text=(AppCompatTextView)findViewById(R.id.destination);
        time_text=(AppCompatTextView)findViewById(R.id.startTimeText);
        host_name=(AppCompatTextView)findViewById(R.id.hostNameText);

        host_address_text=(TextView)findViewById(R.id.hostAddressText);

        back_nav=(ImageButton)findViewById(R.id.backNav);
        travelling_with=(ImageButton)findViewById(R.id.travellingPersonView);
        host_location_map=(ImageButton)findViewById(R.id.addressMapView);
        back_nav.setOnClickListener(this);
        travelling_with.setOnClickListener(this);
        host_location_map.setOnClickListener(this);

        host_name_row=(TableRow)findViewById(R.id.rowHostName);
        host_address_row=(TableRow)findViewById(R.id.rowHostAddress);
        travelling_with_row=(TableRow)findViewById(R.id.rowTravellingWith);

        end_trip=(Button)findViewById(R.id.endTrip);
        end_trip.setOnClickListener(this);

        //Get data from Intent
        data=(Trip)getIntent().getSerializableExtra("Trip");
        ArrayList<Person> t=(ArrayList<Person>)getIntent().getSerializableExtra("People");
        if(t!=null)
            travelling_with_people=t;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backNav:
                setResult(RESULT_OK,null);
                finish();
                break;
            case R.id.travellingPersonView:
                //Open Current Friend list and pass the person array list
                setupTravellingWithActivity();
                break;
            case R.id.addressMapView:
                //Open the map view to track the person
                break;
            case R.id.endTrip:
                //End Trip
                endTripFirebase();
                setResult(END_TRIP,null);
                finish();
                break;
        }
    }

    /********************* Custom Functions ************************/
    private void setupTripDetail(){
        source_text.setText(data.getSource_address());
        destination_text.setText(data.getDestination_address());
        time_text.setText(data.getTime());
        if(!data.getIsOwner()){
            host_name.setText(data.getOwner_string());
            if(data.getOwner_address()!=null){
                host_address_text.setText(data.getOwner_address());
            }
        }else{
            host_name_row.setVisibility(View.GONE);
            host_address_row.setVisibility(View.GONE);
            if(travelling_with_people!=null)
                travelling_with_row.setVisibility(View.VISIBLE);

        }
    }

    private void setupTravellingWithActivity(){
        Intent i=new Intent(TripDetail.this, CurrentFriendList.class);
        i.putExtra("FriendDetailList",travelling_with_people);
        i.putExtra("Source","Trip");
        startActivity(i);
    }

    private void endTripFirebase(){
        DatabaseReference trip_ref= FirebaseDatabase.getInstance().getReference("Trips");
        trip_ref.child(getSharedPreferences("user_credentials", 0).getString("Id", null)).removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed Removing Trip "+e.toString());
            }
        });
    }
}
