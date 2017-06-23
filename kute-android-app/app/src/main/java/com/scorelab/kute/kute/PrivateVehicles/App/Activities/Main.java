package com.scorelab.kute.kute.PrivateVehicles.App.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.HomeBaseFragment;
import com.scorelab.kute.kute.R;


public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView route_request_number;
    private final String TAG="MainPrivateVehicle";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        route_request_number=(TextView)findViewById(R.id.numberRouteRequests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        /****************************** Drawer Layout Setup ************************/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /******************************* End Of Drawer Setup **************************/
        /********************* Load Home Base Fragment**********/
        getSupportFragmentManager().beginTransaction().replace(R.id.frameDrawer,new HomeBaseFragment()).commit();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("user_credentials", 0); // 0 - for private mode
        Log.d("SharedPreference",pref.getString("Profile_Image",null));
        //Register yourself to the firebase db
        registerFirebaseDbSelf(pref);


    }



    /************* Functions handling the navigation drawer **************/
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    /********************* End of nav drawer functions *********************/
    public void registerFirebaseDbSelf(SharedPreferences pref)
    {
        Person temp=new Person(pref.getString("Name",null),pref.getString("Id",null),pref.getString("Profile_Image",null));
        DatabaseReference root= FirebaseDatabase.getInstance().getReference();
        DatabaseReference users=root.child("Users");
        Log.d(TAG,"Saving Self To db");
        users.child(temp.id).setValue(temp).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Firebase Self Add Error:"+e.toString());
            }
        });




    }
}
