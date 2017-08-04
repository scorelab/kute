package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes;

import android.app.TimePickerDialog;
import java.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 27/07/17.
 */

public class SelfRouteDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Boolean isEditLayoutDrawn=false;
    ImageButton backNav,editButton;
    final String TAG="SelfRouteDetail";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail_self_view);
        connectViews();
        setupRouteDetail();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editButton:
                if(!isEditLayoutDrawn){
                    editButton.setImageResource(R.drawable.ic_done_white_24dp);
                }
                else {
                    editButton.setImageResource(R.drawable.ic_mode_edit_white_24dp);
                }
                break;
            case R.id.backNav:
                setupTimePickerDialog();
                Log.d(TAG,"TimePicker");
                //finish();
                break;
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

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    public void connectViews(){
        editButton=(ImageButton)findViewById(R.id.editButton);
        backNav=(ImageButton)findViewById(R.id.backNav);
        backNav.setOnClickListener(this);
        editButton.setOnClickListener(this);
    }


    public void setupRouteDetail(){

    }
}
