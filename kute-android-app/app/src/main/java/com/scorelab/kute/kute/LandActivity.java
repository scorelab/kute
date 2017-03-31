package com.scorelab.kute.kute;

import android.app.Activity;
import android.app.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scorelab.kute.kute.Activity.FragmentUI.PublishFragment;
import com.scorelab.kute.kute.Activity.FragmentUI.TrackFragment;
import com.scorelab.kute.kute.Activity.TaskSelection;
import com.scorelab.kute.kute.Services.BacKService;
import com.scorelab.kute.kute.Util.ImageHandler;
import com.scorelab.kute.kute.Util.MessageKey;

public class LandActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    GoogleMap mGoogleMap;
    ImageView userProfileImage;
    static int SelectTaskActivityCode = 100;
    public static android.support.v4.app.FragmentManager fmn;
    ServiceDataReceiver serviceDataReceiver;
    int applicationTaskStatus=MessageKey.InitShow;
    String keyvehicle=null;
    Marker trackerMarker=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        fmn = getSupportFragmentManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        serviceDataReceiver=new ServiceDataReceiver(null);
        Intent intent = new Intent(this, BacKService.class);
        intent.putExtra("receiver", serviceDataReceiver);
        startService(intent);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent taskselect = new Intent(LandActivity.this, TaskSelection.class);
                startActivityForResult(taskselect, SelectTaskActivityCode);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navigation_header_View = navigationView.getHeaderView(0);
        userProfileImage = (ImageView) navigation_header_View.findViewById(R.id.userProfile);


        try {

            Bitmap userimg = ImageHandler.getUserImage(getSharedPreferences(ImageHandler.MainKey, MODE_PRIVATE));
            if (userimg == null) {
                userProfileImage.setImageResource(R.drawable.defuser);
            } else {
                userimg = Bitmap.createScaledBitmap(userimg, 200, 200, true);
                userProfileImage.setImageBitmap(userimg);
            }

            //userProfileImage.
        } catch (Exception e) {
            e.printStackTrace();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mainMapView);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.land, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectTaskActivityCode) {
            if (resultCode == Activity.RESULT_OK) {
                //Toast.makeText(getApplicationContext(),"+ "+data.getStringExtra("type")+" "+data.getStringExtra("vehname")+" "+data.getStringExtra("Activity"),Toast.LENGTH_LONG).show();
                handleTask(data.getStringExtra("Activity"), data.getStringExtra("type"), data.getStringExtra("vehname"),data.getStringExtra("vehkey"));
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    public void handleTask(String activity, String type, String vehname,String vehkey) {
        Fragment fr;
        Intent intent = new Intent();
        keyvehicle=vehkey;
        Toast.makeText(getApplicationContext(),"-- "+vehkey,Toast.LENGTH_LONG).show();
        if (activity.equals("PublishMe")) {
            //fr = new PublishFragment();
            intent.putExtra(MessageKey.intenetKeyTrackStatus,"publish");
            if(type.equals("train")){
                applicationTaskStatus=MessageKey.PublishTrain;
                intent.putExtra(MessageKey.intenetKeyTrackVehicle,"Trains");
            }else if(type.equals("bus")){
                applicationTaskStatus=MessageKey.PublishBus;
                intent.putExtra(MessageKey.intenetKeyTrackVehicle,"Bus");
            }
        } else if (activity.equals("TrackMe")) {
            //fr = new TrackFragment();
            intent.putExtra(MessageKey.intenetKeyTrackStatus,"track");
            if(type.equals("train")){
                applicationTaskStatus=MessageKey.TrackTrain;
                intent.putExtra(MessageKey.intenetKeyTrackVehicle,"Trains");
            }else if(type.equals("bus")){
                applicationTaskStatus=MessageKey.TrackBus;
                intent.putExtra(MessageKey.intenetKeyTrackVehicle,"Bus");
            }
        } else {
            fr = new PublishFragment();
        }
        intent.putExtra(MessageKey.vehiclekeyindex,keyvehicle);
        intent.setAction(MessageKey.activityserviceintentName);
        sendBroadcast(intent);
        Toast.makeText(getApplicationContext(),"Done Getting task",Toast.LENGTH_LONG).show();

//        fr=new PublishFragment();
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_place, fr);
//        fragmentTransaction.commit();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
    }


    class ServiceDataReceiver extends ResultReceiver{

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public ServiceDataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            runOnUiThread(new UpDateMapFromData(resultData,resultCode));
        }
    }

    class UpDateMapFromData implements Runnable{

        Bundle datatoupdate;
        int rescode;
        public UpDateMapFromData(Bundle data,int ResultCode){
         datatoupdate=data;
            rescode=ResultCode;
        }
        @Override
        public void run() {
            if(rescode== MessageKey.MyLocationUpdate) {
                //Toast.makeText(getApplicationContext(), "+++" + "Got data " + ((Location) datatoupdate.getParcelable("location")).getLongitude(), Toast.LENGTH_LONG).show();
            }
            else if(rescode==MessageKey.FireBaseTrackUpdate){
                //Toast.makeText(getApplicationContext(),"DataChnaged "+datatoupdate.getDouble("lat")+" "+datatoupdate.getDouble("lon"),Toast.LENGTH_LONG).show();

                if(applicationTaskStatus==MessageKey.TrackTrain){

                    LatLng newLoc=new LatLng(datatoupdate.getDouble("lat"),datatoupdate.getDouble("lon"));
                    if(trackerMarker==null) {
                        trackerMarker=mGoogleMap.addMarker(new MarkerOptions().position(newLoc).title(datatoupdate.getString("TrainName")));
                    }
                    else{
                        trackerMarker.setPosition(newLoc);
                        trackerMarker.setTitle(datatoupdate.getString("TrainName"));
                    }

                    //Location trainloc=(Location) datatoupdate.getParcelable("locationlist");
                }
                else if(applicationTaskStatus==MessageKey.TrackBus){

                }
            }
        }
    }
}
