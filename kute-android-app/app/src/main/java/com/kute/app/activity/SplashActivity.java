package com.kute.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.kute.app.R;

/**
 * Created by charith on 6/18/16.
 */
public class SplashActivity extends Activity {

    Animation move;
    ImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        myImageView = (ImageView) findViewById(R.id.myImage);
        move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        myImageView.startAnimation(move);

        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent openSignInActivity = new Intent("app.android.com.kute.app.SignInActivity");
                    startActivity(openSignInActivity);
                }
            }
        };
        timer.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
