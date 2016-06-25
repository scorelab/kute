package com.kute.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kute.app.R;

/**
 * Created by charith on 6/18/16.
 */
public class SplashActivity extends Activity {

    Animation move,showup;
    ImageView myImageView;
    RelativeLayout logindata;
    RelativeLayout mainlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        myImageView = (ImageView) findViewById(R.id.myImage);
        move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        showup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loginshow);
        myImageView.startAnimation(move);

        mainlayout=(RelativeLayout)findViewById(R.id.splash_mainlayout);
        logindata=(RelativeLayout)findViewById(R.id.logincontent);
        logindata.setVisibility(View.INVISIBLE);
//        showup.setFillAfter(true);
        logindata.startAnimation(showup);

        new AnimationTimer().execute();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    class AnimationTimer extends AsyncTask<Void,String ,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            logindata.setVisibility(View.INVISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),"Welcome to Kute",Toast.LENGTH_LONG).show();

            logindata.setVisibility(View.VISIBLE);



        }
    }

}

