package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Main;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Utils.NotificationActivity;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Notification;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Trip;
import com.scorelab.kute.kute.PrivateVehicles.App.Utils.VolleySingleton;
import com.scorelab.kute.kute.R;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;


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
    String source_address,destination_address;
    String destination_cords,source_cords;
    HandlerThread reverse_geocode_thread;
    Handler reverse_geocode_handler;
    Button action_button;
    String action;
    ProgressDialog progress_dialog;
    RequestQueue request_queue;
    boolean is_progress_dialog_visible=false;


    //Overrides
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_a_ride_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        destination_string="null";
        source_string="null";
        source_cords="null";
        destination_cords="null";
        CoordinatorLayout root=(CoordinatorLayout)findViewById(R.id.rootView);
        action=getIntent().getStringExtra("Action");

        reverse_geocode_thread=new HandlerThread("ReverseGeocode");
        reverse_geocode_thread.start();
        reverse_geocode_handler=new Handler(reverse_geocode_thread.getLooper());

        destination=(CardView)findViewById(R.id.destination);
        source=(CardView)findViewById(R.id.source);
        source_text=(TextView)findViewById(R.id.startText);
        destination_text=(TextView)findViewById(R.id.destinationText);
        destination.setCardElevation(32);
        source.setAlpha((float)0.8);
        schedule_trip=(ImageButton)findViewById(R.id.scheduleTrip);
        action_button=(Button)findViewById(R.id.actionButton);
        back_nav=(ImageButton)findViewById(R.id.backNav);
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Registering Trip Request..");
        progress_dialog.setCanceledOnTouchOutside(false);
        back_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        request_queue= VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
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
                    i.putExtra("source",source_string);
                    i.putExtra("destination",destination_string);
                    i.putExtra("destinationCords",destination_cords);
                    i.putExtra("sourceCords",source_cords);
                    i.putExtra("sourceAdd",source_address);
                    i.putExtra("destinationAdd",destination_address);
                    startActivity(i);

                }else {
                    if(destination_string.equals("null") || source_string.equals("null")){
                        Toast.makeText(StartRide.this,"Please Enter Start and End Points",Toast.LENGTH_LONG).show();
                    }else {
                        is_progress_dialog_visible=true;
                        progress_dialog.show();
                        Trip t = new Trip(source_address, destination_address, source_string, destination_string, source_cords, destination_cords, "7", false);
                        String self_id=getSharedPreferences("user_credentials", 0).getString("Id", null);
                        FirebaseDatabase.getInstance().getReference("Temporarytrips").child(self_id).setValue(t).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"Error Adding temporary trip "+e.toString());
                            }
                        });
                        requestServer(getResources().getString(R.string.server_url)+"matchTrip?personId="+self_id+"&Initiator="+"rider");
                    }
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
                i.putExtra("destinationCords",destination_cords);
                i.putExtra("sourceCords",source_cords);
                i.putExtra("sourceAdd",source_address);
                i.putExtra("destinationAdd",destination_address);
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
                                source_cords=Double.toString(location.getLatitude())+","+Double.toString(location.getLongitude());
                                source_string="Current Location";
                                reverseGeocodeSource(location,5);


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
                source_cords=Double.toString(place.getLatLng().latitude)+","+Double.toString(place.getLatLng().longitude);
                source_address=place.getAddress().toString();
            }
            else if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
                destination_string=place.getName().toString();
                destination_text.setText(destination_string);
                destination_cords=Double.toString(place.getLatLng().latitude)+","+Double.toString(place.getLatLng().longitude);
                destination_address=place.getAddress().toString();
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

    //Reverse Geocode and Send Data to Firebase
    public void reverseGeocodeSource(final Location location,final Integer index){
        Runnable r=new Runnable() {
            @Override
            public void run() {
                Log.i("ReverseGeocodeFirebase","Started runnable");
                String address="null";
                Geocoder geocoder= new Geocoder(getBaseContext(), Locale.ENGLISH);
                Double latitude=location.getLatitude();
                Double longitude=location.getLongitude();

                try {

                    //Place your latitude and longitude
                    List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);

                    if(addresses != null) {

                        Address fetchedAddress = addresses.get(0);
                        StringBuilder strAddress = new StringBuilder();


                        for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                            strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                        }
                        address=strAddress.toString();
                        source_address=address;
                        source_string=fetchedAddress.getAddressLine(1);
                        Log.d("ReverseGeocodeFirebase","The current Address is"+ address);
                        Log.d("ReverseGeocodeFirebase","The current source string  is"+ source_string);
                    }

                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Could not get address..!", Toast.LENGTH_LONG).show();
                }
            }

        };
        reverse_geocode_handler.post(r);
    }
    //Request Server
    private void requestServer(final String url){
        final StringRequest mrequest=new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Parse the response from server
                        if(is_progress_dialog_visible){
                            progress_dialog.dismiss();
                        }
                        Log.d(TAG,"Response from server :" +response);
                        Intent i=new Intent(getBaseContext(), Main.class);
                        startActivity(i);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //give an option to retry
                        showMessageTryCancel("Confirmation to server failed..Try Again!",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestServer(url);
                            }
                        });

                    }
                });
        request_queue.add(mrequest);
        if(!is_progress_dialog_visible) {
            is_progress_dialog_visible = true;
            progress_dialog.show();
        }

    }
    //Show try again dialog
    private void showMessageTryCancel(String message, DialogInterface.OnClickListener tryListener) {
        new AlertDialog.Builder(StartRide.this)
                .setMessage(message)
                .setPositiveButton("Try Again", tryListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


}
