package com.kute.app.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.kute.app.Bussiness.Train;
import com.kute.app.R;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private Button shareLocation, showLocation;
    private ImageButton trainButton, busButton, carButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Firebase.setAndroidContext(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);

        shareLocation = (Button) findViewById(R.id.share_button);
        showLocation = (Button) findViewById(R.id.show_button);

        trainButton = (ImageButton) findViewById(R.id.train_button);
        busButton = (ImageButton) findViewById(R.id.bus_button);
        carButton = (ImageButton) findViewById(R.id.car_button);

        setStates(false);

        shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStates(true);
            }
        });

        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setStates(true);
                Intent shareThis = new Intent(getApplicationContext(),
                        ShowLocationActivity.class);
                startActivity(shareThis);
                finish();
            }
        });

        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareThis = new Intent(getApplicationContext(),
                        ShareLocationActivity.class);
                startActivity(shareThis);
                finish();
            }
        });

        busButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareThis = new Intent(getApplicationContext(),
                        ShareLocationActivity.class);
                startActivity(shareThis);
                finish();
            }
        });

        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareThis = new Intent(getApplicationContext(),
                        ShareLocationActivity.class);
                startActivity(shareThis);
                finish();
            }
        });
        setTrains();
       // getTrains();


    }
    public void setTrains(){
        Firebase ref = new Firebase("https://kute-37f82.firebaseio.com/android/TrainsNo/TrainData");
        Firebase alanRef = ref.child("SrilankanTrains").child("1");
        alanRef.child("fullName").setValue("UdarataManike");
        alanRef.child("Start").setValue("Colombo");
        alanRef.child("StartTime").setValue("5.00");
        alanRef.child("End").setValue("Badulla");
        alanRef.child("EndTime").setValue("7.00");

        alanRef = ref.child("SrilankanTrains").child("2");
        alanRef.child("fullName").setValue("Galukumari");
        alanRef.child("Start").setValue("Colombo");
        alanRef.child("StartTime").setValue("6.00");
        alanRef.child("End").setValue("Galle");
        alanRef.child("EndTime").setValue("8.00");

        alanRef = ref.child("SrilankanTrains").child("3");
        alanRef.child("fullName").setValue("Sudu");
        alanRef.child("Start").setValue("Colombo");
        alanRef.child("StartTime").setValue("6.00");
        alanRef.child("End").setValue("Mathara");
        alanRef.child("EndTime").setValue("8.00");



        alanRef.push();

    }

    private void setStates(Boolean state) {

        trainButton.setEnabled(state);
        busButton.setEnabled(state);
        carButton.setEnabled(state);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.sign_out:
                Intent goBack = new Intent(getApplicationContext(),
                        SplashActivity.class);
                startActivity(goBack);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
