package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes;

import android.app.TimePickerDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel.AddTrip;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Utils.DaysPicker;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by nipunarora on 30/07/17.
 */

public class AddRouteActivity extends AppCompatActivity implements View.OnClickListener{
    TextView time;
    AppCompatEditText name,seats;
    AppCompatTextView source,destination;
    ArrayList<Boolean>days;
    ImageButton backnav,set_days,add_button;
    RelativeLayout time_layout;
    private final String TAG="AddRouteActivity";
    private final int result_code_days_picker=0x2;
    private final int add_route_activity_code=0x1;
    String source_name_string,destination_name_string;
    String source_cords,destination_cords;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE=01;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION=03;
    /********** Overrides ***********/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_route);
        days=null;
        //Initialise the views
        name=(AppCompatEditText)findViewById(R.id.routeName);
        source=(AppCompatTextView) findViewById(R.id.startPlace);
        destination=(AppCompatTextView) findViewById(R.id.destination);
        destination.setOnClickListener(this);
        source.setOnClickListener(this);
        time=(TextView)findViewById(R.id.startTime);
        seats=(AppCompatEditText)findViewById(R.id.seatsAvailable);
        backnav=(ImageButton)findViewById(R.id.backNav);
        backnav.setOnClickListener(this);
        add_button=(ImageButton)findViewById(R.id.addButton);
        add_button.setOnClickListener(this);
        set_days=(ImageButton)findViewById(R.id.setDaysButton);
        set_days.setOnClickListener(this);
        time_layout=(RelativeLayout)findViewById(R.id.timeLayout);
        time_layout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backNav:
                finish();
                break;
            case R.id.addButton:
                addRouteToFirebase();
                finish();
                break;
            case R.id.setDaysButton:
                setupDayPicker();
                break;
            case R.id.timeLayout:
                //setup the time picker
                setupTimePicker();
                break;
            case R.id.destination:
                startPlacesActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
                break;
            case R.id.startPlace:
                startPlacesActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE);
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

    /************** Custom Functions ****************/
    private void addRouteToFirebase() {
        //Get user name from credentials
        String self_id = getSharedPreferences("user_credentials", 0).getString("Id", null);
        //Get data from view
        String route_name = name.getText().toString();
        String source_string = source.getText().toString();
        String destination_string = destination.getText().toString();
        String _time = time.getText().toString();
        String number_of_seats = seats.getText().toString();
        Route temp=null;
        if (number_of_seats != "") {
            try {
                temp = new Route(route_name, source_string, destination_string, Integer.parseInt(number_of_seats), days, _time,source_name_string,destination_name_string,source_cords,destination_cords);
            } catch (Exception e) {
                Log.i(TAG, "addRouteToFirebase :" + e.toString());
            }
        } else {
             temp = new Route(route_name, source_string, destination_string, 0, days, _time,source_name_string,destination_name_string,source_cords,destination_cords);
        }

        //Upload to firebase
        Log.d(TAG,"Self Id is:"+self_id);
        DatabaseReference db_ref= FirebaseDatabase.getInstance().getReference("Routes/"+self_id);
        String route_id=db_ref.push().getKey();
        temp.setId(route_id);
        db_ref.child(route_id).setValue(temp).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"Failure uploading route to firebase Exception :"+e.toString());
            }
        });
        //Return the result from the activity
        Intent i=new Intent();
        i.putExtra("Route",temp);
        setResult(add_route_activity_code,i);
    }

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
                        Toast.makeText(AddRouteActivity.this,Integer.toString(hourOfDay),Toast.LENGTH_SHORT).show();
                        String time_string=Integer.toString(hourOfDay)+":"+Integer.toString(minute);
                        time.setText(time_string);

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void setupDayPicker(){
        //We need to start Day Picker Activity for result
        Intent i=new Intent(this, DaysPicker.class);
        i.putExtra("DaysList",days);
        startActivityForResult(i,result_code_days_picker);
    }

    //Start the PlacesAutocomplete API
    private void startPlacesActivity(int code){
        try {
            Log.d(TAG, "OpenPlacesActivity");
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(AddRouteActivity.this);
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
                source.setText(place.getAddress());
                source_cords=Double.toString(place.getLatLng().latitude)+","+Double.toString(place.getLatLng().longitude);
                source_name_string=place.getName().toString();
            }
            else if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
                destination.setText(place.getAddress());
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

}
