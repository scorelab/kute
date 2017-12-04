package com.scorelab.kute.kute;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.FirebaseDatabase;
import com.hanks.htextview.typer.TyperTextView;
import com.scorelab.kute.kute.Activity.RegisterActivity;
import com.scorelab.kute.kute.Activity.SwitchPrivatePublicActivity;
import com.scorelab.kute.kute.Util.ImageHandler;

public class SplashActivity extends AppCompatActivity {
    static  FirebaseDatabase fb;
    TyperTextView ttv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ttv = (TyperTextView) findViewById(R.id.ttv);
        ttv.setSoundEffectsEnabled(false);
        ttv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TyperTextView)v).animateText("A Commute App for Sri Lanka");
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ttv.performClick();
            }
        },2500);





        new DelayTask().execute(); // This will delay the spalsh scrren and redierct to the login/register screen based
        //on the ststus of the user.

    }

    class DelayTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            }
            catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SharedPreferences sharedPref =getPreferences(Context.MODE_PRIVATE);
            Bitmap userpic = ImageHandler.getUserImage(getSharedPreferences(ImageHandler.MainKey,MODE_PRIVATE));

            if(userpic==null){ //User is not registered in the system.

                Intent regIntent =new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(regIntent);
                finish();
            }
            else{ //User registered in the system.
                Intent switchIntent =new Intent(SplashActivity.this, SwitchPrivatePublicActivity.class);
                startActivity(switchIntent);
                finish();
            }



        }
    }
}
