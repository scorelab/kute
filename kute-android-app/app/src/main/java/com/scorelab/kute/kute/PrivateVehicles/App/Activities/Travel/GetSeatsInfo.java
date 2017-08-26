package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Main;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Trip;
import com.scorelab.kute.kute.PrivateVehicles.App.Utils.VolleySingleton;
import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 12/08/17.
 */

public class GetSeatsInfo extends AppCompatActivity implements View.OnClickListener {
    ImageButton back_nav;
    Button go_button;
    String source_cords,dest_cords;
    String source_string,destination_string;
    String source_address,destination_address;
    AppCompatEditText seats;
    ProgressDialog progress_dialog;
    RequestQueue request_queue;
    boolean is_progress_dialog_visible=false;
    private final String TAG="GetSeatsActivity";

    //Overrides

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_seats_info);
        back_nav=(ImageButton)findViewById(R.id.backNav);
        go_button=(Button)findViewById(R.id.goButton);
        back_nav.setOnClickListener(this);
        go_button.setOnClickListener(this);
        Bundle b=getIntent().getExtras();
        source_string=(String) b.get("source");
        destination_string=(String)b.get("destination");
        source_address=(String)b.get("sourceAdd");
        destination_address=(String)b.get("destinationAdd");
        dest_cords=(String)b.get("destinationCords");
        source_cords=(String)b.get("sourceCords");
        seats=(AppCompatEditText)findViewById(R.id.seatsAvailable);
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Registering Trip Request..");
        progress_dialog.setCanceledOnTouchOutside(false);
        request_queue= VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();


        //Get the selected latlng for source and destination
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backNav:
                finish();
                break;
            case R.id.goButton:
                //Add a trip
                addTrip();
                break;

        }
    }

    private void addTrip(){
        if(seats.getText().equals("") || seats.getText()==null){
            Toast.makeText(this,"Enter the No of Seats Available",Toast.LENGTH_LONG).show();
        }else {
            is_progress_dialog_visible=true;
            progress_dialog.show();
            Trip t = new Trip(source_address, destination_address, source_string, destination_string, source_cords, dest_cords, "7", true,Integer.parseInt(seats.getText().toString()));
            String self_id=getSharedPreferences("user_credentials", 0).getString("Id", null);
            FirebaseDatabase.getInstance().getReference("Trips").child(self_id).setValue(t).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"Error Adding temporary trip "+e.toString());
                }
            });
            String url=getResources().getString(R.string.server_url)+"matchTrip?personId="+self_id+"&Initiator="+"owner";
            Log.d(TAG,"The url is +"+url);
            requestServer(url);
        }

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
        new AlertDialog.Builder(GetSeatsInfo.this)
                .setMessage(message)
                .setPositiveButton("Try Again", tryListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



}
