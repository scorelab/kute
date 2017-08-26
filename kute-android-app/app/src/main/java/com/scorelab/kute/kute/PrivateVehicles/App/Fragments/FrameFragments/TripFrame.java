package com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel.TripDetail;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Trip;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.FragmentMail;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by nipunarora on 18/06/17.
 */

public class TripFrame extends Fragment implements View.OnClickListener {
    //TODO load data source from Fragment Args
    Trip data_model;
    View v;
    LinearLayout header_layout,header_net_layout;
    RelativeLayout address_layout,hostname_layout,host_address_layout,time_layout,excuse_text_layout;
    TextView text_from,text_to,host_name,host_address_text,time_text,excuse_text;
    private final String ACTION_TRIP_FOUND="TRIPFOUND";
    private final String ACTION_ADDRESS_UPDATED="ADDRESSUPDATED";

    private final int TRIP_DETAIL_ACTIVITY=711;
    private final int END_TRIP=111;
    ArrayList<Person> travelling_with=null;
    boolean trip_found=false;
    CardView main_card;

    public TripFrame() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.trip_frame, container, false);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Connect basic views
        excuse_text_layout=(RelativeLayout) v.findViewById(R.id.excuseTextLayout);
        excuse_text=(TextView)v.findViewById(R.id.excuseText);
        main_card=(CardView)v.findViewById(R.id.mainCard);
        main_card.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.mainCard:
                if(trip_found){
                    Intent i=new Intent(getContext(), TripDetail.class);
                    i.putExtra("Trip",data_model);
                    if(travelling_with!=null)
                        i.putExtra("People",travelling_with);
                    startActivityForResult(i,TRIP_DETAIL_ACTIVITY);
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TRIP_DETAIL_ACTIVITY && resultCode==END_TRIP){
            toggleGenericTripViews(false);
        }
    }

    /************************** Custom functions **********************/
    public void onReceive(String Action,Object attachments){
        switch(Action){
            case ACTION_TRIP_FOUND:
                //Setup the trip layout
                setupTripLayout(attachments);
                break;
            case ACTION_ADDRESS_UPDATED:
                //Update the user address
                updateHostAddress((String)attachments);
                break;
        }
    }

    private void connectAuxillaryViews(){
        header_layout=(LinearLayout) v.findViewById(R.id.headerLayout);
        header_net_layout=(LinearLayout) v.findViewById(R.id.headerNetLayout);
        address_layout=(RelativeLayout) v.findViewById(R.id.addressLayout);
        hostname_layout=(RelativeLayout) v.findViewById(R.id.hostNameLayout);
        time_layout=(RelativeLayout) v.findViewById(R.id.startTimeLayout);
        host_address_layout=(RelativeLayout)v.findViewById(R.id.hostAddressLayout);

        text_from=(TextView)v.findViewById(R.id.textFrom);
        text_to=(TextView)v.findViewById(R.id.textTo);
        host_name=(TextView)v.findViewById(R.id.hostName);
        host_address_text=(TextView)v.findViewById(R.id.hostAddressText);
        time_text=(TextView)v.findViewById(R.id.startTimeText);
    }

    private void toggleGenericTripViews(boolean check){
        if(check) {
            //remove the current views
            header_layout.setVisibility(View.GONE);
            excuse_text_layout.setVisibility(View.GONE);
            //enable other views
            header_net_layout.setVisibility(View.VISIBLE);
            address_layout.setVisibility(View.VISIBLE);
            time_layout.setVisibility(View.VISIBLE);
        }else {
            header_net_layout.setVisibility(View.GONE);
            address_layout.setVisibility(View.GONE);
            time_layout.setVisibility(View.GONE);
            hostname_layout.setVisibility(View.GONE);
            header_layout.setVisibility(View.VISIBLE);
            excuse_text_layout.setVisibility(View.VISIBLE);
            excuse_text.setText("You do not have an active trip right now \n Start A New Trip from \n My Routes Tab or from the button below");
            //enable other views

        }

    }

    private void toggleHostViews(boolean check){

        hostname_layout.setVisibility(View.VISIBLE);
        if(check)
            host_address_layout.setVisibility(View.VISIBLE);
    }

    //Setup the trip layout once trip is retrieved from firebase
    private void setupTripLayout(Object trip_map){
        //Case when the user has no active trips
        if (trip_map==null){
            excuse_text.setText("You do not have an active trip right now \n Start A New Trip from \n My Routes Tab or from the button below");
        }
        else{
            connectAuxillaryViews();
            toggleGenericTripViews(true);
            trip_found=true;
            HashMap<String,Object> data= (HashMap<String,Object>)trip_map;
            Trip trip_object=(Trip)data.get("Trip");
            data_model=trip_object;
            Route route_object=(Route)data.get("Route");

            //Setup from and to texts
            if(route_object!=null){
                text_from.setText(route_object.source_name);
                text_to.setText(route_object.destination_name);
            }else{
                text_from.setText(trip_object.getSource_name());
                text_to.setText(trip_object.getDestination_name());
            }


            //Setup time
            if(!trip_object.getTime().equals("7"))
                time_text.setText(trip_object.getTime());
            else {
                time_layout.setVisibility(View.GONE);

            }

            //Connect the auxillary views

            //Setup host name if not a host
            if(!trip_object.getIsOwner()){
                host_name.setText(trip_object.getOwner_string());
                if (trip_object.getOwner_address()!=null)
                    toggleHostViews(true);
                else
                    toggleHostViews(false);
            }else {
                travelling_with=(ArrayList<Person>)data.get("People");
            }

        }
    }

    //Updating the owner address
    private void updateHostAddress(String address){
        host_address_layout.setVisibility(View.VISIBLE);
        host_address_text.setText(address);
    }

}
