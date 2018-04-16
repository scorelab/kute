package com.scorelab.kute.kute.Activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.scorelab.kute.kute.R;
import com.scorelab.kute.kute.SplashActivity;
import com.scorelab.kute.kute.Util.ImageHandler;

import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    RequestQueue rq;
    private SignInButton mSignInButton;

    private GoogleApiClient mGoogleApiClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    // [START declare_auth_listener] Facebook
    private FirebaseAuth.AuthStateListener mAuthListener;

    private CallbackManager mCallbackManager; //Facebook


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_with_google:
                signIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
// An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication Done." + task.getResult().getUser().getDisplayName() + " - " + task.getResult().getUser().getDisplayName() + " - " + task.getResult().getUser().getEmail() + " - " + task.getResult().getUser().getPhotoUrl().getPath() + " - ",
                                    Toast.LENGTH_SHORT).show();
                            getImage(task.getResult().getUser().getPhotoUrl().toString());

                            Intent intentdone = new Intent(RegisterActivity.this, SplashActivity.class);
                            startActivity(intentdone);

                        }
                    }
                });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.testreg);

            // Assign fields
            mSignInButton = findViewById(R.id.login_with_google);

            // Set click listeners
            mSignInButton.setOnClickListener(this);

            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            // Initialize FirebaseAuth
            mFirebaseAuth = FirebaseAuth.getInstance();


            //Facebook
            mAuth = FirebaseAuth.getInstance();
            // [START auth_state_listener]
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        Toast.makeText(getApplicationContext(), "Facebook  User " + user.getPhotoUrl(), Toast.LENGTH_LONG).show();
                        getImage(user.getPhotoUrl().toString());


                        Intent intentdone = new Intent(RegisterActivity.this, SplashActivity.class);
                        startActivity(intentdone);
                        finish();


                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                        Toast.makeText(getApplicationContext(), "Facebook  User signed out", Toast.LENGTH_LONG).show();
                    }
                    // [START_EXCLUDE]


                    //updateUI(user);
                    // [END_EXCLUDE]
                }
            };

            mCallbackManager = CallbackManager.Factory.create();
            //LoginButton loginButton = findViewById(R.id.connectWithFbButton);
            //loginButton.setReadPermissions("email", "public_profile", "user_friends");

            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                private ProfileTracker mProfileTracker;

                @Override
                public void onSuccess(LoginResult loginResult) {
                    try {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        //Saving Facebook Credentials of the user
                        if (Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    // profile2 is the new profile
                                    Log.d("check", Profile.getCurrentProfile().toString());
                                    saveFacebookProfile(profile2);
                                    mProfileTracker.stopTracking();
                                }
                            };
                            mProfileTracker.startTracking();
                            // no need to call startTracking() on mProfileTracker
                            // because it is called by its constructor, internally.
                        } else {
                            Log.d("check", Profile.getCurrentProfile().toString());
                            saveFacebookProfile(Profile.getCurrentProfile());
                        }

                        Log.d(TAG, "Facebook Login:Saved Credentials to Shared Prefs");
                        //Auth with Firebase
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onCancel() {
                    try {
                        Log.d(TAG, "facebook:onCancel");
                        // [START_EXCLUDE]
                        Toast.makeText(getApplicationContext(), "Facebook  Cancel", Toast.LENGTH_LONG).show();
                        //updateUI(null);
                        // [END_EXCLUDE]
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onError(FacebookException error) {
                    try {

                        Log.d(TAG, "facebook:onError", error);
                        // [START_EXCLUDE]
                        Toast.makeText(getApplicationContext(), "Facebook  Error " + error.getMessage(), Toast.LENGTH_LONG).show();
                        //updateUI(null);
                        // [END_EXCLUDE]
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            });

            Button newLoginButton = findViewById(R.id.connectWithFbButtonNew);
            final Activity thisActivity = this;
            newLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().logInWithReadPermissions(thisActivity, Arrays.asList("email", "public_profile", "user_friends"));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try {
            Toolbar toolbar = findViewById(R.id.toolbar_sign_in);
            setSupportActionBar(toolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle("Register or log in");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        rq = Volley.newRequestQueue(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onSupportNavigateUp() {
        finishAffinity();
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth != null){
            mAuth.addAuthStateListener(mAuthListener);

        }
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        Toast.makeText(getApplicationContext(), "Facebook  show dialog", Toast.LENGTH_LONG).show();
        //showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithCredential", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // [START_EXCLUDE]
                            Toast.makeText(getApplicationContext(), "Facebook  hide dialog", Toast.LENGTH_LONG).show();
                            //hideProgressDialog();
                            // [END_EXCLUDE]
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                });
    }
    // [END auth_with_facebook]


    //Facebook
    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(getApplicationContext(), "You have been Signout from the Kute", Toast.LENGTH_LONG).show();
        //updateUI(null);
    }

    public void saveFacebookProfile(Profile pf) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("user_credentials", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Login_Method", "Facebook");
        editor.putString("Name", pf.getName());
        editor.putString("Id", pf.getId());
        editor.putString("Profile_Image", "null");
        editor.putBoolean("Register_db", true);
        editor.putBoolean("Sync_Friends_db", true);
        editor.apply();
    }

    public void getImage(String url) {
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ImageHandler.saveImageToprefrence(getSharedPreferences(ImageHandler.MainKey, MODE_PRIVATE), response);
                ImageView iv = findViewById(R.id.imageView);
                iv.setImageBitmap(response);
            }
        }, 0, 0, null, null);
        rq.add(ir);

    }

}
