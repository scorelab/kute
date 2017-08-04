package com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes.RouteDetail;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 15/06/17.
 */

public class RouteFrame extends Fragment {
    //TODO load data source from Fragment Args
    // This Fragment has been created to load in a single route frame required at places such as the home tab for starred routes
    // or in the persondetail
    View v;
    TextView route_name,from,to;
    AppCompatTextView number_seats;
    CardView route_card;
    private final String TAG="RouteFrameFrag";
    public RouteFrame() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.my_route_item, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get the arguement from the args
        try {
            Bundle b = getArguments();
            final Route route = (Route) b.getSerializable("Route");
            //Initialise the views
            route_name = (TextView) v.findViewById(R.id.RouteHead);
            from = (TextView) v.findViewById(R.id.textFrom);
            to = (TextView) v.findViewById(R.id.textTo);
            number_seats = (AppCompatTextView) v.findViewById(R.id.noOfSeats);
            route_card = (CardView) v.findViewById(R.id.routeCard);
            route_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), RouteDetail.class);
                    i.putExtra("Route", route);
                    startActivity(i);
                }
            });
            //Set Content to views
            route_name.setText(route.getName());
            from.setText(route.getSource());
            to.setText(route.getDestination());
            number_seats.setText(Integer.toString(route.getSeats_available()));
        }catch (Exception e){
            Log.i(TAG,e.toString());
        }
    }

}

