package com.kute.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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
    TextView forgot;
    boolean res;
    Logger logger;
    int startPosition = -1;
    static boolean go = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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

        myImageView = (ImageView) findViewById(R.id.myImage);
        myImageView.animate().translationY(0).start();

        mainlayout = (RelativeLayout) findViewById(R.id.splash_mainlayout);

        logindata = (RelativeLayout) findViewById(R.id.logincontent);


        initFirebase();
        if(mAuth.getCurrentUser() != null || go){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    go = false;
                    gotoNextView();
                    showWelcomeMessage();
                }
            },1000);
        } else
            myImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int[] location = new int[2];
                    myImageView.getLocationOnScreen(location);
                    logindata.setVisibility(View.VISIBLE);
                    if (startPosition == -1)
                        startPosition = location[1];
                    else {
                        myImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int start = startPosition;
                        int end = location[1];
                        myImageView.setTranslationY(start - end);
                        logindata.setAlpha(0f);
                        myImageView.animate().translationY(0).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                logindata.animate().alpha(1).setDuration(250).start();
                                showWelcomeMessage();
                            }
                        }).setDuration(500).setStartDelay(1000).start();
                    }
                }
            });

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

    private void showWelcomeMessage() {
        Toast.makeText(getApplicationContext(), "Welcome to Kute", Toast.LENGTH_LONG).show();
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
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
    mAuth.createUserWithEmailAndPassword(uname, pwd)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registration failed",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "You have successfully Registered",
                                Toast.LENGTH_SHORT).show();
                    }

                    // ...
                }
            });
}

    public void doLogin(String email,String pwd){

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
                            Toast.makeText(SplashActivity.this, "Sorry Login failed",
                                    Toast.LENGTH_SHORT).show();
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

}

