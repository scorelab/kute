package com.scorelab.kute.kute.Services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.Util.MessageKey;
import com.scorelab.kute.kute.Util.TrainTrackFirebase;

import org.w3c.dom.Comment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nrv on 2/9/17.
 */

public class BacKService extends Service implements LocationListener {
    ResultReceiver resultReceiver;

    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1 * 60 * 1;
    Location location;
    private String provider;
    LocationManager m_locationManager;
    String vehkeyindex=null;
    String PubTrackStatus;
    String Vehicle;
    //FirebaseDatabase database;// = FirebaseDatabase.getInstance();
    DatabaseReference ref;

    // Create reciever object
    private BroadcastReceiver controlrecver = new ControlMessageRecever();

    // Set When broadcast event will fire.
    private IntentFilter filter = new IntentFilter(MessageKey.activityserviceintentName);

    // Register new broadcast receiver

    private String TAG=BacKService.class.getName();


    public BacKService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        this.m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.registerReceiver(controlrecver, filter);
        try {
            ref = FirebaseDatabase.getInstance().getReference();
        }
        catch (Exception e){
            Log.e(TAG,e.getMessage());
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        resultReceiver = intent.getParcelableExtra("receiver");
        this.m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        this.m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);
        this.m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("location",location);
        resultReceiver.send(MessageKey.MyLocationUpdate, bundle);

        if(PubTrackStatus!=null && PubTrackStatus.equals("publish") && vehkeyindex!=null && Vehicle!=null && ref !=null) {
           // Toast.makeText(getApplicationContext(),"e.toString()",Toast.LENGTH_LONG).show();
            Map<String, Object> hopperUpdates = new HashMap<String, Object>();
            hopperUpdates.put("lat", location.getLatitude());
            hopperUpdates.put("lon", location.getLongitude());
            hopperUpdates.put("speed", location.getSpeed());
            ref.updateChildren(hopperUpdates);
        }
        //Toast.makeText(getApplicationContext(),"e.toString() "+(vehkeyindex!=null) +" "+ (Vehicle!=null) + " "+ (ref !=null) ,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Bundle bundle = new Bundle();
        bundle.putString("provider",provider);
        bundle.putInt("status",status);
        bundle.putBundle("extras",extras);
        resultReceiver.send(MessageKey.onStatusChangedLocation, bundle);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class ControlMessageRecever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) { //ToDo add thread and synchronise
           // try {
                String vehkey = intent.getStringExtra(MessageKey.vehiclekeyindex); //Firebase index of the vehicle
                vehkeyindex = vehkey;
        // try{
                PubTrackStatus = intent.getStringExtra(MessageKey.intenetKeyTrackStatus); //pUBLISH OR TRACK
                Vehicle = intent.getStringExtra(MessageKey.intenetKeyTrackVehicle); //pUBLISH OR TRACK
       // try{
                if (PubTrackStatus.equals("publish")) {
                        ref = FirebaseDatabase.getInstance().getReference(Vehicle + "/" + vehkeyindex + "/");
                    ref.removeEventListener(childEventListener);
                } else if (PubTrackStatus.equals("track")) {
                    ref = FirebaseDatabase.getInstance().getReference(Vehicle + "/" );
                    ref.addChildEventListener(childEventListener);
                }
        }
    }


    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

            // A new comment has been added, add it to the displayed list
            //Comment comment = dataSnapshot.getValue(Comment.class);

            // ...
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                double speed;
                double lat = (Double) dataSnapshot.child("lat").getValue();
                double lon = (Double) dataSnapshot.child("lon").getValue();
                try {
                    speed = (Double) dataSnapshot.child("speed").getValue();
                }
                catch (Exception e){
                    speed = (Long) dataSnapshot.child("speed").getValue();
                }
                String TrainName = (String) dataSnapshot.child("TrainName").getValue();
            Bundle bundle = new Bundle();
            bundle.putDouble("lat",lat);
            bundle.putDouble("lon",lon);
            bundle.putDouble("speed",speed);
            bundle.putString("TrainName",TrainName);
            resultReceiver.send(MessageKey.FireBaseTrackUpdate, bundle);


            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.


            // ...
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so remove it.
            //String commentKey = dataSnapshot.getKey();

            // ...
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

            // A comment has changed position, use the key to determine if we are
            // displaying this comment and if so move it.
            //Comment movedComment = dataSnapshot.getValue(Comment.class);
            //String commentKey = dataSnapshot.getKey();

            // ...
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException());

        }
    };


}
