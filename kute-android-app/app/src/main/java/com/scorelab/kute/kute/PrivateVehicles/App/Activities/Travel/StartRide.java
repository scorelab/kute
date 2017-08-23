package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.scorelab.kute.kute.R;


public class StartRide extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {
    GoogleMap gmap;
    CardView destination,source;
    TextView source_text,destination_text;
    private FusedLocationProviderClient mFusedLocationClient;
    private final String TAG="FindRideActivity";
    Integer requestCode=77;
    String permission = Manifest.permission.ACCESS_FINE_LOCATION;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE=01;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION=02;
    ImageButton schedule_trip,back_nav;
    String destination_string,source_string;
    LatLng destination_latlng,source_latlng;
    Button action_button;
    String action;


    //Overrides
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_a_ride_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        destination_string=null;
        source_string=null;
        destination_latlng=null;
        source_latlng=null;
        CoordinatorLayout root=(CoordinatorLayout)findViewById(R.id.rootView);
        action=getIntent().getStringExtra("Action");

        destination=(CardView)findViewById(R.id.destination);
        source=(CardView)findViewById(R.id.source);
        source_text=(TextView)findViewById(R.id.startText);
        destination_text=(TextView)findViewById(R.id.destinationText);
        destination.setCardElevation(32);
        source.setAlpha((float)0.8);
        schedule_trip=(ImageButton)findViewById(R.id.scheduleTrip);
        action_button=(Button)findViewById(R.id.actionButton);
        back_nav=(ImageButton)findViewById(R.id.backNav);
        back_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(action.equals("Owner")){
            //Show a snackbar indiacting that you can start a trip from your routes as well
            Snackbar snackbar = Snackbar
                    .make(root, "You can start a ride from your Routes as well", Snackbar.LENGTH_LONG);

            snackbar.show();
            schedule_trip.setVisibility(View.GONE);
            action_button.setText("Start Ride");
        }else if(action.equals("Finder")){
            action_button.setText("Find Ride");
        }
        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(action.equals("Owner")){
                    Intent i=new Intent(StartRide.this,GetSeatsInfo.class);
                    i.putExtra("destination-LatLng",destination_latlng);
                    i.putExtra("source-LatLng",source_latlng);
                    startActivity(i);
                }
            }
        });

        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destination.setCardElevation(32);
                destination.setAlpha(1);
                source.setCardElevation(16);
                source.setAlpha((float)0.8);
                startPlacesActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
            }
        });
        source_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source.setCardElevation(32);
                source.setAlpha((float) 1);
                destination.setCardElevation(16);
                destination.setAlpha((float)0.8);
                startPlacesActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE);
            }
        });

        schedule_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(StartRide.this,AddTrip.class);
                i.putExtra("source",source_string);
                i.putExtra("destination",destination_string);
                i.putExtra("destination-LatLng",destination_latlng);
                i.putExtra("source-LatLng",source_latlng);
                startActivity(i);

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap=googleMap;
        if(checkPermissions())
            getLocation();
        else
            askForPermission(requestCode,permission);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,connectionResult.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Received Permission");
            getLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE || requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
            configurePlace(requestCode,resultCode,data);
        }
    }


    /*********************** Custom methods *******************/
    //Check for permission
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    //A method invoked to ask user for permissions
    private void askForPermission(final Integer requestCode,final String... permission) {
        Boolean did_user_deny_location= ActivityCompat.shouldShowRequestPermissionRationale(StartRide.this, permission[0]);
        if (ContextCompat.checkSelfPermission(StartRide.this, permission[0]) == PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            getLocation();
        } else {
            //The if condition below would work if the user has denied one particular permission or both
            if (did_user_deny_location) {
                //When user denied access to both the camera and external storage
                setupPermissionDialog(requestCode, "We need to access your location to allow you to share your location with others", permission);
            }
            else {
                setupPermissionDialog(requestCode,"Allow access to location", permission);
            }
        }
    }

    private void setupPermissionDialog(final Integer requestCode,String message,final String... permission)
    {
        showMessageOKCancel(message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(StartRide.this, permission, requestCode);
                    }
                });
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(StartRide.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void getLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // ...
                                LatLng home=new LatLng(location.getLatitude(),location.getLongitude());
                                gmap.addMarker(new MarkerOptions().position(home).title("Home"));
                                gmap.moveCamera(CameraUpdateFactory.newLatLng(home));
                                gmap.moveCamera(CameraUpdateFactory.zoomTo(17.0f));
                                updateStartPointAddress(location);
                                source_latlng=new LatLng(location.getLatitude(),location.getLongitude());
                                source_string="Current Location";

                            }
                        }
                    });
        }catch (SecurityException e){
            Log.d(TAG,"Security Exception"+e.toString());

        }
    }

    //Update start point address with current address
    private void updateStartPointAddress(Location location){
       source_text.setText("Current Location");
    }
    //Start the PlacesAutocomplete API
    private void startPlacesActivity(int code){
        try {
            Log.d(TAG, "OpenPlacesActivity");
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(StartRide.this);
            startActivityForResult(intent, code);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Log.d(TAG, "onTouch: " + e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Log.d(TAG, "onTouch: " + e.toString());
        }
    }
    //Configure the place received from GOOGLE PLACES API
    private void configurePlace(int requestCode,int resultCode,Intent data){
        if (resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE) {
                source_string=place.getName().toString();
                source_text.setText(source_string);
                source_latlng=place.getLatLng();
            }
            else if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
                destination_string=place.getAddress().toString();
                destination_text.setText(destination_string);
                destination_latlng=place.getLatLng();
            }
            Log.i(TAG, "Place: " + place.getAddress());
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.
            Log.i(TAG, status.getStatusMessage());

        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }

    }

}
