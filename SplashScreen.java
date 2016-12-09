package com.kute.app.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.kute.app.R;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread thread = new Thread(){
            @Override
        public void run(){
                try {
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    startActivity(intent);
                    finish();

                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
}
