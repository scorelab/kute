package com.kute.app.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kute.app.Helpers.Logger;
import com.kute.app.R;

/**
 * Created by charith on 6/18/16.
 */

public class SplashActivity extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG="Splash Activity";
    Animation move, showup;
    ImageView myImageView;
    RelativeLayout logindata;
    RelativeLayout mainlayout;
    EditText userName, passWord;
    Button login, register;
    FloatingActionButton helpFab;
    TextView forgot;
    boolean res;
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
        //forgot = (TextView) findViewById(R.id.txtForgotPassword);
        helpFab = (FloatingActionButton) findViewById(R.id.help_btn);

        myImageView = (ImageView) findViewById(R.id.myImage);
        myImageView.startAnimation(move);

        mainlayout = (RelativeLayout) findViewById(R.id.splash_mainlayout);

        logindata = (RelativeLayout) findViewById(R.id.logincontent);
        logindata.startAnimation(showup);

        new AnimationTimer().execute();
        initFirebase();


        if(mAuth.getCurrentUser()!=null){
            gotoNextView();
        }



        // Login action
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Implement login validation and related tasks
                String user = userName.getText().toString();
                String pass = passWord.getText().toString();

                if (true) {
                    doLogin(user,pass);
                    if(res){
                        Toast.makeText(getApplicationContext(), "Welcome " + user, Toast.LENGTH_LONG).show();
                        logger.setUserLogged(true);
                        gotoNextView();

                    }

                } else {

                        Toast.makeText(getApplicationContext(),
                                "Please click Signup and try again! :)", Toast.LENGTH_LONG).show();

                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Implement signup and related tasks
                doRegister(userName.getText().toString(),passWord.getText().toString());
            }
        });
    }

    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void doRegister(String uname,String pwd){
    Log.e(TAG,uname+" "+pwd);
        Snackbar.make(helpFab, "Registering...", Snackbar.LENGTH_INDEFINITE).show();
    mAuth.createUserWithEmailAndPassword(uname, pwd)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Snackbar.make(helpFab, "Registration failed",
                                Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(helpFab, "You have successfully Registered",
                                Snackbar.LENGTH_SHORT).show();
                    }

                    // ...
                }
            });
}

    public void doLogin(String email,String pwd){

        Snackbar.make(helpFab, "Logging in...", Snackbar.LENGTH_INDEFINITE).show();
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Snackbar.make(helpFab, "Sorry Login failed",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        res = task.isSuccessful();


                        // ...
                    }
                });

    }
    //ToDo set user logged in session to unlimited.
    //ToDo username password validation pwd has spme required length
    public void gotoNextView(){
        Intent mainac = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(mainac);
        finish();
    }
    public void authtestfunction(){


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

            logindata.setVisibility(View.VISIBLE);
            super.onPostExecute(aVoid);
        }
    }

}

