package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    AppCompatEditText name,from,to,seats;
    ArrayList<Boolean>days;
    ImageButton backnav,set_days,add_button;
    RelativeLayout time_layout;
    private final String TAG="AddRouteActivity";
    private final int result_code_days_picker=0x2;
    private final int add_route_activity_code=0x1;
    /********** Overrides ***********/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_route);
        days=null;
        //Initialise the views
        name=(AppCompatEditText)findViewById(R.id.routeName);
        from=(AppCompatEditText)findViewById(R.id.startPlace);
        to=(AppCompatEditText)findViewById(R.id.destination);
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==result_code_days_picker){
            days=(ArrayList<Boolean>)data.getSerializableExtra("DayList");
            Log.i(TAG,"OnActivityResult :Received result from Days Picker "+days.get(1).toString());
        }
    }

    /************** Custom Functions ****************/
    private void addRouteToFirebase() {
        //Get user name from credentials
        String self_name = getSharedPreferences("user_credentials", 0).getString("Name", null);
        //Get data from view
        String route_name = name.getText().toString();
        String source = from.getText().toString();
        String destination = to.getText().toString();
        String _time = time.getText().toString();
        String number_of_seats = seats.getText().toString();
        Route temp=null;
        if (number_of_seats != "") {
            try {
                temp = new Route(route_name, source, destination, Integer.parseInt(number_of_seats), days, _time);
            } catch (Exception e) {
                Log.i(TAG, "addRouteToFirebase :" + e.toString());
            }
        } else {
             temp = new Route(route_name, source, destination, 0, days, _time);
        }

        //Upload to firebase
        DatabaseReference db_ref= FirebaseDatabase.getInstance().getReference("Routes/"+self_name);
        db_ref.child(db_ref.push().getKey()).setValue(temp).addOnFailureListener(new OnFailureListener() {
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

}
