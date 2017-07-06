package com.scorelab.kute.kute.PrivateVehicles.App.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.HomeBaseFragment;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.UserSelfProfileFragment;
import com.scorelab.kute.kute.PrivateVehicles.App.Services.SyncFacebookFriendsToFirebase;
import com.scorelab.kute.kute.R;

import org.json.JSONArray;
import org.json.JSONObject;


public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView route_request_number;
    ImageButton route_request,search;
    private final String TAG = "MainPrivateVehicle";
    private final String Action = SyncFacebookFriendsToFirebase.class.getName() + "Complete";

    /*********************** Overrides ************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        route_request_number = (TextView) findViewById(R.id.numberRouteRequests);
        route_request=(ImageButton)findViewById(R.id.routeRequests);
        search=(ImageButton)findViewById(R.id.searchIcon);
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
        getSupportFragmentManager().beginTransaction().replace(R.id.frameDrawer, new HomeBaseFragment(), "HomeBaseFragment").commit();
        navigationView.setCheckedItem(R.id.Home);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("user_credentials", 0); // 0 - for private mode
        Log.d("SharedPreference", pref.getString("Profile_Image", null));


    }

    /****************** End of Overrides ********************/
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /************* Functions handling the navigation drawer **************/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.Home) {
            // Handle the camera action
        } else if (id == R.id.Profile) {
            setupProfileFragment();
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
    /******************** Custom Functions ********************************/
    private void setupProfileFragment()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameDrawer, new UserSelfProfileFragment(), "UserSelfProfileFragment").commit();
        search.setImageResource(R.drawable.ic_mode_edit_white_24dp);
        route_request_number.setVisibility(View.GONE);
        route_request.setVisibility(View.GONE);
    }

}
