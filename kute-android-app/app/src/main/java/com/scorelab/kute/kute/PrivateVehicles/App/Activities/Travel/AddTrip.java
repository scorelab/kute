package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Main;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes.AddRouteActivity;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Utils.DaysPicker;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by nipunarora on 12/08/17.
 */

public class AddTrip extends AppCompatActivity implements View.OnClickListener{
    TextView source,destination,time;
    ImageButton back_nav,register_trip,days_selector;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE=01;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION=02;
    private final String TAG="AddTripActivity";
    private final int result_code_days_picker=0007;
    ArrayList<Boolean>days;
    String source_cords,dest_cords;
    String source_string,destination_string;
    String source_address,destination_address;
    String time_string;


    //Override

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trip);
        days=null;
        //Initialise the views
        destination=(TextView)findViewById(R.id.destination);
        source=(TextView)findViewById(R.id.startPlace);
        Bundle b=getIntent().getExtras();
        source_string=(String) b.get("source");
        destination_string=(String)b.get("destination");
        source_address=(String)b.get("sourceAdd");
        destination_address=(String)b.get("destinationAdd");

        if(!source_string.equals("null")){
            source.setText(source_string);
        }
        if (!destination_string.equals("null")){
            destination.setText(destination_string);
        }

        dest_cords=(String)b.get("destinationCords");
        source_cords=(String)b.get("sourceCords");


        back_nav=(ImageButton)findViewById(R.id.backNav);
        time=(TextView)findViewById(R.id.startTime);
        days_selector=(ImageButton)findViewById(R.id.setDaysButton);
        register_trip=(ImageButton)findViewById(R.id.addButton);
        destination.setOnClickListener(this);
        source.setOnClickListener(this);
        back_nav.setOnClickListener(this);
        days_selector.setOnClickListener(this);
        time.setOnClickListener(this);
        register_trip.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.destination:
                startPlacesActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
                break;
            case R.id.startPlace:
                startPlacesActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE);
                break;
            case R.id.backNav:
                finish();
                break;
            case R.id.setDaysButton:
                setupDayPicker();
                break;
            case R.id.startTime:
                setupTimePicker();
                break;
            case R.id.addButton:
                addTrip();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"OnActivityResult code "+Integer.toString(requestCode));
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE || requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
            configurePlace(requestCode,resultCode,data);
        }
        if(resultCode==result_code_days_picker){
            days=(ArrayList<Boolean>)data.getSerializableExtra("DayList");
            Log.i(TAG,"OnActivityResult :Received result from Days Picker "+days.get(1).toString());
        }
    }


    /*********************************** Custom Functions *******************/

    //Start the PlacesAutocomplete API
    private void startPlacesActivity(int code){
        try {
            Log.d(TAG, "OpenPlacesActivity");
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(AddTrip.this);
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
                source.setText(place.getAddress());
                source_cords=Double.toString(place.getLatLng().latitude)+","+Double.toString(place.getLatLng().longitude);
                source_address=place.getAddress().toString();
            }
            else if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
                destination_string=place.getAddress().toString();
                destination.setText(place.getAddress());
                dest_cords=Double.toString(place.getLatLng().latitude)+","+Double.toString(place.getLatLng().longitude);
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
    //Start Material Time Picker
    private void setupTimePicker(){
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
                        Toast.makeText(AddTrip.this,Integer.toString(hourOfDay),Toast.LENGTH_SHORT).show();
                        String time_string1=Integer.toString(hourOfDay)+":"+Integer.toString(minute);
                        time.setText(time_string1);
                        time_string=time_string1;

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    //Start the day picker
    private void setupDayPicker(){
        //We need to start Day Picker Activity for result
        Intent i=new Intent(this, DaysPicker.class);
        i.putExtra("DaysList",days);
        startActivityForResult(i,result_code_days_picker);
    }

    //Add trip to firebase
    private void addTrip(){
        //Connect to firebase and add

        //Return to the homescreen
        Intent i=new Intent(getApplicationContext(), Main.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

}
