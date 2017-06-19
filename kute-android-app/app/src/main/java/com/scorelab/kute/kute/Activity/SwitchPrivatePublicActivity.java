package com.scorelab.kute.kute.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.scorelab.kute.kute.LandActivity;
import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 19/06/17.
 */

public class SwitchPrivatePublicActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton publicVeh,privateVeh;
    /*****************  Overrides ***************/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_public_private_activity_layout);
        /************* Initialise Views *************************/

        publicVeh=(ImageButton)findViewById(R.id.publicVehicle);
        privateVeh=(ImageButton)findViewById(R.id.privateVehicle);
        publicVeh.setOnClickListener(this);
        privateVeh.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.publicVehicle:
                Intent i=new Intent(this, LandActivity.class);
                startActivity(i);
                break;
            case R.id.privateVehicle:
                Toast.makeText(this,"Request For privateVehicle",Toast.LENGTH_SHORT).show();
                //TODO open the private vehicle activity
                break;
            default:
                Toast.makeText(this,"Not Clickable",Toast.LENGTH_SHORT).show();
                break;

        }
    }

    /*************** End Of overrides ***********/
}
