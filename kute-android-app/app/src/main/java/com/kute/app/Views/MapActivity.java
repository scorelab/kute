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

import com.kute.app.R;

public class MapActivity extends AppCompatActivity {

    private Button shareLocation, showLocation;
    private ImageButton trainButton, busButton, carButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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
                setStates(true);
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
