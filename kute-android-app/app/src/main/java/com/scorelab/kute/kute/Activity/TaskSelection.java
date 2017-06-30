package com.scorelab.kute.kute.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scorelab.kute.kute.R;

/**
 * Created by nrv on 2/8/17.
 */

public class TaskSelection extends AppCompatActivity {

    Button trackMeImage;
    Button PublishMeImage;
    AlertDialog dialogSelectVehicle;
    static int TrackMeCode=1;
    static int PublishCode=2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskselection);

        trackMeImage=(Button)findViewById(R.id.trackVehicle);
        PublishMeImage=(Button)findViewById(R.id.UpdateMe);

        trackMeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectvehicle=new Intent(TaskSelection.this,VehicleSelection.class);
                startActivityForResult(selectvehicle,TrackMeCode);
            }
        });

        PublishMeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectvehicle=new Intent(TaskSelection.this,VehicleSelection.class);
                startActivityForResult(selectvehicle,PublishCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TrackMeCode){
            if(resultCode== Activity.RESULT_OK){

                data.putExtra("Activity","TrackMe");
                setResult(Activity.RESULT_OK,data);
                finish();


            }
            else if(resultCode==Activity.RESULT_CANCELED){
                setResult(Activity.RESULT_CANCELED,data);
                finish();

            }
        }
        else if(requestCode==PublishCode){
            if(resultCode== Activity.RESULT_OK){
                data.putExtra("Activity","PublishMe");
                setResult(Activity.RESULT_OK,data);
                finish();
            }
            else if(resultCode==Activity.RESULT_CANCELED){
                setResult(Activity.RESULT_CANCELED,data);
                finish();
            }
        }

    }
}
