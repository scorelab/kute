package com.scorelab.kute.kute;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.Activity.RegisterActivity;
import com.scorelab.kute.kute.Activity.SwitchPrivatePublicActivity;
import com.scorelab.kute.kute.Util.ImageHandler;

public class SplashActivity extends AppCompatActivity {
    static  FirebaseDatabase fb;
    ImageView imageView;
    AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = (ImageView)findViewById(R.id.imageView);
        if(imageView == null) throw new AssertionError();
        imageView.setBackgroundResource(R.drawable.loading_animation);
        imageView.setVisibility(View.VISIBLE);

        anim = (AnimationDrawable)imageView.getBackground();
        anim.start();

        new DelayTask().execute(); // This will delay the spalsh scrren and redierct to the login/register screen based
       //on the ststus of the user.

    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//
//
//
//    }

    class DelayTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(4000);
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
            }
            else{ //User registered in the system.
                Intent switchIntent =new Intent(SplashActivity.this, SwitchPrivatePublicActivity.class);
                startActivity(switchIntent);
            }



        }
    }
}
