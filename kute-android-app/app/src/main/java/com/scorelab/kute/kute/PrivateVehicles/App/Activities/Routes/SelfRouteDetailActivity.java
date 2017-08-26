package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Main;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel.GetSeatsInfo;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Utils.DaysPicker;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Trip;
import com.scorelab.kute.kute.PrivateVehicles.App.Utils.VolleySingleton;
import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 27/07/17.
 */

public class SelfRouteDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Boolean isEditLayoutDrawn=false;
    ImageButton backNav,editButton,days_button;
    final String TAG="SelfRouteDetail";
    private final int ROUTE_DELETE_CODE=10;
    ArrayList<Boolean>days;
    TextView route_name,time,no_seats;
    AppCompatTextView from,to;
    AppCompatEditText no_of_seats_edit,route_name_edit;
    String source_name_string,destination_name_string;
    String source_cords,destination_cords;
    String id;
    String time_text;
    String source_address,destination_address;
    Button delete_route,start_trip;
    ProgressDialog progress_dialog;
    RequestQueue request_queue;
    boolean is_progress_dialog_visible=false;

    private final int result_code_days_picker=0x2;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE=01;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION=03;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail_self_view);
        connectViews();
        setupInitialDetails();
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Registering Trip Request..");
        progress_dialog.setCanceledOnTouchOutside(false);
        request_queue= VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editButton:
                if(!isEditLayoutDrawn){
                    editButton.setImageResource(R.drawable.ic_done_white_24dp);
                    isEditLayoutDrawn=true;
                    setupEditLayout();
                }
                else {
                    editButton.setImageResource(R.drawable.ic_mode_edit_white_24dp);
                    isEditLayoutDrawn=false;
                    setupDetailsLayout();
                }
                break;
            case R.id.backNav:
                setResult(1000,null);
                finish();
                break;
            case R.id.startTime:
                setupTimePickerDialog();
                break;
            case R.id.startPlace:
                if(isEditLayoutDrawn)
                startPlacesActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE);
                break;
            case R.id.destination:
                if (isEditLayoutDrawn)
                startPlacesActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
                break;
            case R.id.daysSelect:
                setupDayPicker();
                break;
            case R.id.deleteRoute:
                deleteRouteFirebase();
                break;
            case R.id.startTrip:
                setupTrip();
                break;
                
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==result_code_days_picker){
            days=(ArrayList<Boolean>)data.getSerializableExtra("DayList");
            Log.i(TAG,"OnActivityResult :Received result from Days Picker "+days.get(1).toString());
        }else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE || requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
            configurePlace(requestCode,resultCode,data);
        }
    }


    /********************** Custom Functions *********************/
    //Creates the dialog for time picking
    public void setupTimePickerDialog(){
        // Get Current Time
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.TimePickerTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        Toast.makeText(SelfRouteDetailActivity.this,Integer.toString(hourOfDay),Toast.LENGTH_SHORT).show();
                        String time_string=Integer.toString(hourOfDay)+":"+Integer.toString(minute);
                        time.setText(time_string);

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    public void connectViews(){
        editButton=(ImageButton)findViewById(R.id.editButton);
        backNav=(ImageButton)findViewById(R.id.backNav);
        route_name=(TextView)findViewById(R.id.routeNameText);
        no_seats=(TextView)findViewById(R.id.noSeats);
        to=(AppCompatTextView) findViewById(R.id.destination);
        from=(AppCompatTextView)findViewById(R.id.startPlace);
        time=(TextView)findViewById(R.id.startTime);
        days_button=(ImageButton)findViewById(R.id.daysSelect);
        days_button.setOnClickListener(this);
        delete_route=(Button)findViewById(R.id.deleteRoute);
        start_trip=(Button)findViewById(R.id.startTrip);
        delete_route.setOnClickListener(this);
        start_trip.setOnClickListener(this);
        no_of_seats_edit=(AppCompatEditText)findViewById(R.id.noSeatEdit);
        route_name_edit=(AppCompatEditText)findViewById(R.id.routeNameEdit); 
        to.setOnClickListener(this);
        from.setOnClickListener(this);
        backNav.setOnClickListener(this);
        editButton.setOnClickListener(this);
    }
    

    private void setupInitialDetails(){
        Route route=(Route)getIntent().getSerializableExtra("Route");
        //Initialise the views
        //Connect with data model
        id=route.getId();
        route_name.setText(route.getName());
        from.setText(route.getSource());
        to.setText(route.getDestination());
        no_seats.setText(route.getSeats_available().toString());
        time.setText(route.getTime());
        source_address=route.getSource();
        destination_address=route.getDestination();
        source_name_string=route.source_name;
        destination_name_string=route.destination_name;
        source_cords=route.source_cords;
        destination_cords=route.destination_cords;
        time_text=route.getTime();
        //Call method to get the details of the days
        if(route.getDays()!=null) {
            //Initialise the dayslist
            days=route.getDays();
        }
    }

    //Start the PlacesAutocomplete API
    private void startPlacesActivity(int code){
        try {
            Log.d(TAG, "OpenPlacesActivity");
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(SelfRouteDetailActivity.this);
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
                source_address=place.getAddress().toString();
                from.setText(place.getAddress());
                source_cords=Double.toString(place.getLatLng().latitude)+","+Double.toString(place.getLatLng().longitude);
                source_name_string=place.getName().toString();
            }
            else if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
                destination_address=place.getAddress().toString();
                to.setText(place.getAddress());
                destination_cords=Double.toString(place.getLatLng().latitude)+","+Double.toString(place.getLatLng().longitude);
                destination_name_string=place.getName().toString();
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

    //Setup the day picker activity
    private void setupDayPicker(){
        //We need to start Day Picker Activity for result
        Intent i=new Intent(this, DaysPicker.class);
        i.putExtra("DaysList",days);
        startActivityForResult(i,result_code_days_picker);
    }

    //Setup the edit layout
    private void setupEditLayout(){
       route_name.setVisibility(View.GONE);
        route_name_edit.setVisibility(View.VISIBLE);
        route_name_edit.setText(route_name.getText());
        no_seats.setVisibility(View.GONE);
        no_of_seats_edit.setVisibility(View.VISIBLE);
        no_of_seats_edit.setText(no_seats.getText());
        start_trip.setVisibility(View.GONE);
    }

    //Setup the details layout
    private void setupDetailsLayout(){
        String mod_name=route_name_edit.getText().toString();
        String mod_seats=no_of_seats_edit.getText().toString();
        route_name_edit.setVisibility(View.GONE);
        route_name.setText(mod_name);
        route_name.setVisibility(View.VISIBLE);
        no_of_seats_edit.setVisibility(View.GONE);
        no_seats.setText(mod_seats);
        no_seats.setVisibility(View.VISIBLE);
        start_trip.setVisibility(View.VISIBLE);

        updateRouteFirebase(mod_name,mod_seats);
    }

    //Update Route at firebase
    private void updateRouteFirebase(String name,String no_of_seats){
        String self_id = getSharedPreferences("user_credentials", 0).getString("Id", null);
        Route temp=new Route(name, source_address, destination_address, Integer.parseInt(no_of_seats), days,time.getText().toString(),source_name_string,destination_name_string,source_cords,destination_cords);
        temp.setId(id);
        DatabaseReference db_ref= FirebaseDatabase.getInstance().getReference("Routes/"+self_id);
        db_ref.child(id).setValue(temp).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"Failure uploading route to firebase Exception :"+e.toString());
            }
        });

    }

    //Delete Route From Firebase
    private void deleteRouteFirebase(){
        String self_id = getSharedPreferences("user_credentials", 0).getString("Id", null);
        DatabaseReference db_ref= FirebaseDatabase.getInstance().getReference("Routes/"+self_id);
        db_ref.child(id).removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Error in route deletion "+e.toString());
            }
        });
        setResult(ROUTE_DELETE_CODE,null);
        finish();
    }

    //Setup a trip on firebase
    private void setupTrip(){
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        String time_string=String.format("%d:%d",mHour,mMinute);
        is_progress_dialog_visible=true;
        progress_dialog.show();
        Trip t = new Trip(source_address, destination_address, source_name_string, destination_name_string, source_cords, destination_cords,time_string , true,Integer.parseInt(no_seats.getText().toString()));
        String self_id=getSharedPreferences("user_credentials", 0).getString("Id", null);
        FirebaseDatabase.getInstance().getReference("Trips").child(self_id).setValue(t).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Error Adding trip trip "+e.toString());
            }
        });
        String url=getResources().getString(R.string.server_url)+"matchTrip?personId="+self_id+"&Initiator="+"owner";
        Log.d(TAG,"The url is +"+url);
        requestServer(url);
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
                        Log.d(TAG,"volley Error "+error.toString());
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
        new AlertDialog.Builder(SelfRouteDetailActivity.this)
                .setMessage(message)
                .setPositiveButton("Try Again", tryListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



}
