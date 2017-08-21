package com.scorelab.kute.kute.PrivateVehicles.App.Fragments.HomeScreenTabFragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Travel.StartRide;
import com.scorelab.kute.kute.PrivateVehicles.App.AsyncTasks.LoadTripAsyncTask;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments.FriendFrame;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments.RouteFrame;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments.TripFrame;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;
import com.scorelab.kute.kute.PrivateVehicles.App.Miscelleneous.FabMenu;
import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 06/06/17.
 */

public class HomeTab extends Fragment implements View.OnClickListener,AsyncTaskListener {
    private final String TAG = "HomeTab";
    View v;
    //FAB Menu Items
    Button[] buttons;
    int[] buttonicon;
    TextView[] buttonlabels;
    FragmentManager fm;
    int height, width;//button dimensions
    String[] buttonlabel = {"Start A Trip", "Find A Ride"};
    FabMenu fabMenu;
    int whichAnimation = 0; //variable to determine whether entry animation is to be played or exit animation
    int startPositionX = 0;
    int startPositionY = 0;
    FloatingActionButton fab;
    LoadTripAsyncTask load_trip;
    ScrollView sc;
    private final String ACTION_TRIP_FOUND="TRIPFOUND";
    private final String ACTION_ADDRESS_UPDATED="ADDRESSUPDATED";

    public HomeTab() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_tab_bottomnavigation, container, false);
        fm = getChildFragmentManager();
        load_trip=new LoadTripAsyncTask(this);
        load_trip.execute(getActivity().getSharedPreferences("user_credentials", 0).getString("Id", null));
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*********** Get your top starred routes trips and suggested friends
         **** Set your  Trip,Suggested Friend and Starred route ******/
        //TODO set Args for respective fragments then place the frames
        fm.beginTransaction().replace(R.id.tripFrame, new TripFrame(), "TripFrame").commit();
        fm.beginTransaction().replace(R.id.strarredRouteFrame, new RouteFrame(), "RouteFrame").commit();
        setupFAB();
        sc = (ScrollView) v.findViewById(R.id.homeTabScroll);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onClick(View v) {
        switch ((int) v.getTag()) {
            case 0:
                Intent i = new Intent(getContext(), StartRide.class);
                i.putExtra("Action", "Owner");
                startActivity(i);
                fab.callOnClick();
                break;
            case 1:
                Intent j = new Intent(getContext(), StartRide.class);
                j.putExtra("Action", "Finder");
                startActivity(j);
                fab.callOnClick();
                break;
            default:
                break;

        }
    }

    @Override
    public void onTaskStarted(Object... attachments) {
    }

    @Override
    public void onTaskCompleted(Object attachment) {
        sendMessageToTripFrame(ACTION_TRIP_FOUND,attachment);
        //Log.d(TAG,"Attachment in HomeTab"+attachment.toString());
    }

    @Override
    public void onStop() {
        super.onStop();
        load_trip.cancel(true);
    }

    /******************** Custom Functions *************************/
    //Fab Menu Functions
    private void setupFAB() {
        //initialising FAB menu variables
        height = (int) getResources().getDimension(R.dimen.button_height);
        width = (int) getResources().getDimension(R.dimen.button_width);
        buttons = new Button[2];
        buttonlabels = new TextView[2];
        buttonicon = new int[2];
        buttonicon[0] = R.drawable.steeringwheel;
        buttonicon[1] = R.drawable.automobile1;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        fabMenu = new FabMenu(getContext(), displayMetrics.heightPixels, displayMetrics.widthPixels);
        setupFabMenuButtons();
        //Fab onClick
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whichAnimation == 0) {
                    /**
                     * Getting the center point of floating action button
                     *  to set start point of buttons
                     */
                    startPositionX = (int) v.getX() + 50;
                    startPositionY = (int) v.getY() + 50;
                    fabMenu.setXYStartPosition(startPositionX, startPositionY);
                    for (Button button : buttons) {
                        button.setX(startPositionX);
                        button.setY(startPositionY);
                        button.setVisibility(View.VISIBLE);
                    }
                    for (TextView t : buttonlabels) {
                        t.setX(startPositionX);
                        t.setY(startPositionY);
                        t.setVisibility(View.VISIBLE);

                    }
                    for (int i = 0; i < buttons.length; i++) {
                        fabMenu.playEnterAnimation(buttons[i], i, buttonlabels[i], sc);
                    }
                    whichAnimation = 1;
                } else {
                    for (int i = 0; i < buttons.length; i++) {
                        fabMenu.playExitAnimation(buttons[i], i, buttonlabels[i], sc);
                    }
                    whichAnimation = 0;
                }


            }
        });

    }

    private void setupFabMenuButtons() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button(getContext());
            buttons[i].setLayoutParams(new RelativeLayout.LayoutParams(5, 5));
            buttons[i].setX(0);
            buttons[i].setY(0);
            buttons[i].setTag(i);
            buttons[i].setOnClickListener(this);
            buttons[i].setVisibility(View.INVISIBLE);
            buttons[i].setBackgroundResource(R.drawable.circular_back);
            if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                setButtonBackgroundAPI15(buttons[i], i);
            } else
                setButtonBackgroundAPI16Above(buttons[i], i);
            ((RelativeLayout) v.findViewById(R.id.homeTabRelative)).addView(buttons[i]);
            buttonlabels[i] = new TextView(getContext());
            buttonlabels[i].setLayoutParams(new RelativeLayout.LayoutParams(5, 5));
            buttonlabels[i].setX(0);
            buttonlabels[i].setY(0);
            buttonlabels[i].setText(buttonlabel[i]);
            buttonlabels[i].setVisibility(View.INVISIBLE);
            buttonlabels[i].setTextColor(Color.parseColor("#000000"));
            ((RelativeLayout) v.findViewById(R.id.homeTabRelative)).addView(buttonlabels[i]);


        }
    }

    //Set FAB Menu Button Background Methods
    @TargetApi(15)
    public void setButtonBackgroundAPI15(Button b, int i) {
        b.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), buttonicon[i], null));
    }

    @TargetApi(16)
    public void setButtonBackgroundAPI16Above(Button b, int i) {
        b.setBackground(ResourcesCompat.getDrawable(getResources(), buttonicon[i], null));
    }

    //Send Messages to Trip Frame
    private void sendMessageToTripFrame(String Action,Object attachments) {

        try {
            TripFrame fragment =
                    (TripFrame) getChildFragmentManager().findFragmentByTag("TripFrame");
            fragment.onReceive(Action,attachments);
        } catch (Exception e) {
            Log.d(TAG,"Exception in Send Message to trip frame :"+e.toString());
        }


    }
}
