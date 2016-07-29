package com.kute.app.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dilushi on 7/8/2016.
 */

public class Logger {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREFERENCES_LABEL = "kute";
    private static final String USER_NAME = "Admin";
    private static final String PASSWORD = "pass";
    private static final String LOGGED = "logged";


    @SuppressLint("CommitPrefEdits")
    public Logger(Context context) {

        this.context = context;
        preferences = context.getSharedPreferences(PREFERENCES_LABEL, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    // Setting username
    public void setUserName(String userName) {
        editor.putString(USER_NAME, userName);
        editor.commit();
    }

    // Setting password
    public void setPassword(String password) {
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    // Setting user as logged in
    public void setUserLogged(Boolean state) {
        editor.putBoolean(LOGGED, state);
    }

    // Return logged in state
    public Boolean isUserLogged() {
        return preferences.getBoolean(LOGGED, false);
    }

    // Returning username
    public String getUserName() {
        return preferences.getString(USER_NAME, null);
    }

    // Returning password
    public String getPassword() {
        return preferences.getString(PASSWORD, null);
    }
}