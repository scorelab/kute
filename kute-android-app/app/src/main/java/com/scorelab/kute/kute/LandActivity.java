package com.scorelab.kute.kute;

import android.app.Activity;
import android.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.scorelab.kute.kute.Activity.FragmentUI.PublishFragment;

import com.scorelab.kute.kute.Activity.TaskSelection;
import com.scorelab.kute.kute.Activity.VehicleSelection;
import com.scorelab.kute.kute.Miscelleneous.FabMenu;
import com.scorelab.kute.kute.Services.BacKService;
import com.scorelab.kute.kute.Util.ImageHandler;
import com.scorelab.kute.kute.Util.MessageKey;

public class LandActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {
    GoogleMap mGoogleMap;
    ImageView userProfileImage;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    static int SelectTaskActivityCode = 100;
    public static android.support.v4.app.FragmentManager fmn;
    ServiceDataReceiver serviceDataReceiver;
    int applicationTaskStatus = MessageKey.InitShow;
    String keyvehicle = null;
    Marker trackerMarker = null;

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


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling permission requests
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);


        //initialising FAB menu variables
        height = (int) getResources().getDimension(R.dimen.button_height);
        width = (int) getResources().getDimension(R.dimen.button_width);
        buttons=new Button[2];
        buttonlabels=new TextView[2];

        buttonicon=new int[2];
        buttonicon[0]=R.drawable.compass;
        buttonicon[1]=R.drawable.placeholder;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        fabMenu=new FabMenu(this,displayMetrics.heightPixels,displayMetrics.widthPixels);
        setupFabMenuButtons();
        //Fab onClick
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whichAnimation == 0) {
                    /**
                     * Getting the center point of floating action button
                     *  to set start point of buttons
                     */
                    startPositionX = (int) v.getX() + 50;
                    startPositionY = (int) v.getY() + 50;
                    fabMenu.setXYStartPosition(startPositionX,startPositionY);
                    for (Button button : buttons) {
                        button.setX(startPositionX);
                        button.setY(startPositionY);
                        button.setVisibility(View.VISIBLE);
                    }
                    for (TextView t:buttonlabels)
                    {
                        t.setX(startPositionX);
                        t.setY(startPositionY);
                        t.setVisibility(View.VISIBLE);

                    }
                    for (int i = 0; i < buttons.length; i++) {
                        fabMenu.playEnterAnimation(buttons[i], i,buttonlabels[i]);
                    }
                    whichAnimation = 1;
                } else {
                    for (int i = 0; i < buttons.length; i++) {
                        fabMenu.playExitAnimation(buttons[i], i,buttonlabels[i]);
                    }
                    whichAnimation = 0;
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Setting up navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navigation_header_View = navigationView.getHeaderView(0);

        //show username and email
        TextView user_txt = (TextView) navigation_header_View.findViewById(R.id.nameUser);
        TextView email_txt = (TextView) navigation_header_View.findViewById(R.id.emailUser);
        user_txt.setText(mUser.getDisplayName());
        email_txt.setText(mUser.getEmail());

        //show display photo
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


        //Map area
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

        }else if(id == R.id.nav_logout){
            //logout the user and direct to login page
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            Intent tologin_intent =new Intent(this, RegisterActivity.class);
            startActivity(tologin_intent);

            Toast.makeText(getApplicationContext(),"You have been Signout from the Kute",Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectVehicleActivityCode) {
            if (resultCode == Activity.RESULT_OK) {
                //Toast.makeText(getApplicationContext(),"+ "+data.getStringExtra("type")+" "+data.getStringExtra("vehname")+" "+data.getStringExtra("Activity"),Toast.LENGTH_LONG).show();
                handleTask(data.getStringExtra("Activity"), data.getStringExtra("type"), data.getStringExtra("vehname"), data.getStringExtra("vehkey"));
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    public void handleTask(String activity, String type, String vehname, String vehkey) {
        Fragment fr;
        Intent intent = new Intent();

        keyvehicle = vehkey;
        Toast.makeText(getApplicationContext(), "-- " + vehkey, Toast.LENGTH_LONG).show();

        if (activity.equals("PublishMe")) {
            //fr = new PublishFragment();
            intent.putExtra(MessageKey.intenetKeyTrackStatus, "publish");
            if (type.equals("train")) {
                applicationTaskStatus = MessageKey.PublishTrain;
                intent.putExtra(MessageKey.intenetKeyTrackVehicle, "Trains");
            } else if (type.equals("bus")) {
                applicationTaskStatus = MessageKey.PublishBus;
                intent.putExtra(MessageKey.intenetKeyTrackVehicle, "Bus");
            }
        } else if (activity.equals("TrackMe")) {
            //fr = new TrackFragment();
            intent.putExtra(MessageKey.intenetKeyTrackStatus, "track");
            if (type.equals("train")) {
                applicationTaskStatus = MessageKey.TrackTrain;
                intent.putExtra(MessageKey.intenetKeyTrackVehicle, "Trains");
            } else if (type.equals("bus")) {
                applicationTaskStatus = MessageKey.TrackBus;
                intent.putExtra(MessageKey.intenetKeyTrackVehicle, "Bus");
            }
        } else {
            fr = new PublishFragment();
        }
        intent.putExtra(MessageKey.vehiclekeyindex, keyvehicle);
        intent.setAction(MessageKey.activityserviceintentName);
        sendBroadcast(intent);
        Toast.makeText(getApplicationContext(), "Done Getting task", Toast.LENGTH_LONG).show();

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
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mGoogleMap.animateCamera(cameraUpdate);
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling permission requests
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {/**/}

    @Override
    public void onProviderEnabled(String provider) {/**/}

    @Override
    public void onProviderDisabled(String provider) {/**/}


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

    @Override
    public void onClick(View v) {
        switch ((int) v.getTag()) {
            case 0:
                Toast.makeText(this, "Track My Location", Toast.LENGTH_SHORT).show();
                startActivityForTrack();
                break;
            case 1:
                Toast.makeText(this, "Share My Location", Toast.LENGTH_SHORT).show();
                startActivityForPublish();
                break;
            default:break;

        }
    }
    private void setupFabMenuButtons()
    {
        for (int i = 0; i < buttons.length; i++) {

            buttons[i] = new Button(LandActivity.this);
            buttons[i].setLayoutParams(new RelativeLayout.LayoutParams(5, 5));
            buttons[i].setX(0);
            buttons[i].setY(0);
            buttons[i].setTag(i);
            buttons[i].setOnClickListener(this);
            buttons[i].setVisibility(View.INVISIBLE);
            buttons[i].setBackgroundResource(R.drawable.circular_background);
            buttons[i].setBackground(ResourcesCompat.getDrawable(getResources(),buttonicon[i], null));
            /*buttons[i].setTextColor(Color.WHITE);
            buttons[i].setText(String.valueOf(i + 1));
            buttons[i].setTextSize(20);*/
            ((RelativeLayout) findViewById(R.id.content_land)).addView(buttons[i]);
            buttonlabels[i]=new TextView(this);
            buttonlabels[i].setLayoutParams(new RelativeLayout.LayoutParams(5, 5));
            buttonlabels[i].setX(0);
            buttonlabels[i].setY(0);
            buttonlabels[i].setText(buttonlabel[i]);
            buttonlabels[i].setVisibility(View.INVISIBLE);
            ((RelativeLayout) findViewById(R.id.content_land)).addView(buttonlabels[i]);




        }
    }
    public void  startActivityForTrack()
    {
        Intent taskselect = new Intent(LandActivity.this, VehicleSelection.class);
        taskselect.putExtra("Activity","TrackMe");
        startActivityForResult(taskselect, SelectVehicleActivityCode);
    }
    public void startActivityForPublish()
    {
        Intent taskselect = new Intent(LandActivity.this, VehicleSelection.class);
        taskselect.putExtra("Activity","PublishMe");
        startActivityForResult(taskselect, SelectVehicleActivityCode);

    }
}
