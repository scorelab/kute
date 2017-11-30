package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Utils;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Main;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Notification;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments.FriendFrame;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments.PlaceHolderFragment;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments.TripFrame;
import com.scorelab.kute.kute.PrivateVehicles.App.Utils.VolleySingleton;
import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 23/08/17.
 */

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener {
    // Members
    private final String TAG="notifActivity";
    ImageButton backnav;
    TextView head_text,head_detail,start_place,drop_place,start_head,drop_head;
    FrameLayout person_item;
    TableRow row_start,row_drop,row_start_text,row_drop_text;
    Button confirm_trip;
    boolean isOwner;
    boolean is_progress_dialog_visible=false;
    ProgressDialog progress_dialog;
    Notification not;
    RequestQueue request_queue;

    /********************** Overrides **************/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity);
        head_text=(TextView)findViewById(R.id.headText);
        head_detail=(TextView)findViewById(R.id.headDetailRest);
        start_place=(TextView)findViewById(R.id.startPlace);
        drop_place=(TextView)findViewById(R.id.dropText);
        start_head=(TextView)findViewById(R.id.startHead);
        drop_head=(TextView)findViewById(R.id.dropHead);

        row_start=(TableRow)findViewById(R.id.rowStart);
        row_start_text=(TableRow)findViewById(R.id.rowStartPlace);
        row_drop=(TableRow)findViewById(R.id.rowDrop);
        row_drop_text=(TableRow)findViewById(R.id.rowDropText);

        confirm_trip=(Button)findViewById(R.id.confirmTrip);
        backnav=(ImageButton)findViewById(R.id.backNav);

        progress_dialog = new ProgressDialog(NotificationActivity.this);
        progress_dialog.setCanceledOnTouchOutside(false);

        confirm_trip.setOnClickListener(this);
        backnav.setOnClickListener(this);

        //Setting up a place holder for the person
        PlaceHolderFragment plc=new PlaceHolderFragment();
        Bundle b=new Bundle();
        b.putString("Label","Loading.....");
        plc.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(R.id.personItem, plc, "PlaceHolder").commit();

        not=(Notification)getIntent().getSerializableExtra("Notification");
        processNotification(not);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.backNav:
                Intent i=new Intent(NotificationActivity.this, Main.class);
                startActivity(i);
                finish();
                break;
            case R.id.confirmTrip:
                //Confirm trip
                processTripConfirmation();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(request_queue!=null)
            request_queue.cancelAll("yes");
        if(is_progress_dialog_visible){
            progress_dialog.dismiss();
        }
    }

    /******************************* Custom Functions ********************/
    private void processNotification(Notification not){
        isOwner=getSharedPreferences("user_credentials", 0).getString("Id", null).equals(not.getOwner_id());
        if(isOwner){
            if(not.getStatus().equals("FoundRide")){
                head_text.setText("We found a match for your trip" );
                head_detail.setText("Can pool with you");
                FirebaseDatabase.getInstance().getReference("Users").child(not.getRider_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Person p=dataSnapshot.getValue(Person.class);
                        Bundle b=new Bundle();
                        b.putSerializable("Friend_1",p);
                        FriendFrame ff=new FriendFrame();
                        ff.setArguments(b);
                        getSupportFragmentManager().beginTransaction().replace(R.id.personItem, ff, "Person").commit();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                if(not.getOpposite_name()!=null){
                    start_head.setText(not.getOpposite_name()+"'s Start Point");
                    drop_head.setText(not.getOpposite_name()+"'s Drop Point");
                    if(not.getOpposite_start()!=null){
                        start_place.setText(not.getOpposite_start());
                        drop_place.setText(not.getOpposite_drop());
                    }
                }
            }
        }else{
            if(not.getStatus().equals("FoundRide")){
                //Remove unneccessary views
                row_drop.setVisibility(View.GONE);
                row_drop_text.setVisibility(View.GONE);
                row_start_text.setVisibility(View.GONE);
                row_start.setVisibility(View.GONE);

                head_text.setText("We found a match for your ride" );
                head_detail.setText("Wants to pool with you");
                FirebaseDatabase.getInstance().getReference("Users").child(not.getOwner_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Person p=dataSnapshot.getValue(Person.class);
                        Bundle b=new Bundle();
                        b.putSerializable("Friend_1",p);
                        FriendFrame ff=new FriendFrame();
                        ff.setArguments(b);
                        getSupportFragmentManager().beginTransaction().replace(R.id.personItem, ff, "Person").commit();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

    }

    //Method handling the COnfirm ride click
    private void processTripConfirmation(){
        request_queue= VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        if(isOwner){
            String url=String.format(getResources().getString(R.string.server_url)+"sendNotification?Owner=%s&Rider=%s&notifType=confirmAwait",not.getOwner_id(),not.getRider_id());
            Log.d(TAG,"The url for request is "+url);
            requestServer(url);
        }else {
            String url=String.format(getResources().getString(R.string.server_url)+"sendNotification?Owner=%s&Rider=%s&notifType=confirmed",not.getOwner_id(),not.getRider_id());
            Log.d(TAG,"The url for request is "+url);
            requestServer(url);
        }

    }

    //Request Server
    private void requestServer(final String url){
        final  StringRequest mrequest=new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Parse the response from server
                            if(is_progress_dialog_visible){
                                progress_dialog.dismiss();
                            }
                            Log.d(TAG,"Response from server :" +response);
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
        is_progress_dialog_visible=true;
        progress_dialog.show();

    }
    //Show try again dialog
    private void showMessageTryCancel(String message, DialogInterface.OnClickListener tryListener) {
        new AlertDialog.Builder(NotificationActivity.this)
                .setMessage(message)
                .setPositiveButton("Try Again", tryListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
