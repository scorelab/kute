package com.kute.app.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kute.app.R;
import com.kute.app.Helpers.Logger;

/**
 * Created by charith on 6/18/16.
 */

public class SplashActivity extends Activity {

    Animation move, showup;
    ImageView myImageView;
    RelativeLayout logindata;
    RelativeLayout mainlayout;
    EditText userName, passWord;
    Button login, register;
    Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        logger = new Logger(this);

        move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        showup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loginshow);

        userName = (EditText) findViewById(R.id.username_input);
        passWord = (EditText) findViewById(R.id.password_input);
        login = (Button) findViewById(R.id.login_btn);
        register = (Button) findViewById(R.id.register_btn);

        myImageView = (ImageView) findViewById(R.id.myImage);
        myImageView.startAnimation(move);

        mainlayout = (RelativeLayout) findViewById(R.id.splash_mainlayout);

        logindata = (RelativeLayout) findViewById(R.id.logincontent);
        logindata.startAnimation(showup);

        new AnimationTimer().execute();

        // Login action
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = userName.getText().toString();
                String pass = passWord.getText().toString();

                if (user.equalsIgnoreCase(logger.getUserName())
                        && (pass.contentEquals(logger.getPassword()))) {
                    logger.setUserLogged(true);
                    Intent mainac = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(mainac);
                    finish();
                    Toast.makeText(getApplicationContext(), "Welcome "
                            + user, Toast.LENGTH_LONG).show();

                } else {
                    try {
                        if (!user.equalsIgnoreCase(logger.getUserName())) {
                            userName.setError("Incorrect! Please re-enter Username.");
                        }
                        if (!pass.contentEquals(logger.getPassword())) {
                            passWord.setError("Password Incorrect!");
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(),
                                "Please click Signup and try again! :)", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logger.setUserName("Admin");
                logger.setPassword("pass");
            }
        });
    }

    private class AnimationTimer extends AsyncTask<Void, String, Void> {

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

            Toast.makeText(getApplicationContext(), "Welcome to Kute", Toast.LENGTH_LONG).show();
            logindata.setVisibility(View.VISIBLE);
            super.onPostExecute(aVoid);
        }
    }
}

