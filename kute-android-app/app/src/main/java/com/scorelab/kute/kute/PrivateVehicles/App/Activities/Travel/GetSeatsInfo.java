package com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 12/08/17.
 */

public class GetSeatsInfo extends AppCompatActivity implements View.OnClickListener {
    ImageButton back_nav;
    Button go_button;
    //Overrides

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_seats_info);
        back_nav=(ImageButton)findViewById(R.id.backNav);
        go_button=(Button)findViewById(R.id.goButton);
        back_nav.setOnClickListener(this);
        go_button.setOnClickListener(this);

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
                break;

        }
    }
}
