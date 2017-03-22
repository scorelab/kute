package com.kute.app.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.github.paolorotolo.appintro.AppIntro;
import com.kute.app.Activities.*;
import com.kute.app.Activities.SplashActivity;
import com.kute.app.R;

/**
 * Created by Deniz on 29.12.2016.
 */

public class IntroActivity extends AppIntro {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new Slide01());
        addSlide(new Slide02());
        addSlide(new Slide03());
        addSlide(new Slide04());
        addSlide(new Slide05());

        setSlideOverAnimation();
    }



    private void loadMainActivity(){
        Intent intent = new Intent(IntroActivity.this, SplashActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
        Toast.makeText(getApplicationContext(),"Skip Pressed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v){
        loadMainActivity();
    }
}
