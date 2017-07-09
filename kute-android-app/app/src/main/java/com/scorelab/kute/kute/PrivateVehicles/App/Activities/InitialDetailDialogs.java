package com.scorelab.kute.kute.PrivateVehicles.App.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.InitialDetailDialogFragments.FirstInfoFragment;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.InitialDetailDialogFragments.SecondConatctFragment;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.InitialDetailDialogFragments.ThirdVehicleFragment;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.FragmentToActivityMail;
import com.scorelab.kute.kute.R;


public class InitialDetailDialogs extends AppCompatActivity implements View.OnClickListener,FragmentToActivityMail {
    TextView[]dots;
    LinearLayout dotsLayout;
    Button proceed,back;
    int current_frag;
    String contact,vehicle_name;
    int FORWARD_PROCEED_CODE=07;
    int BACK_PROCEED_CODE=707;
    FragmentManager fragment_manager;
    SharedPreferences pref;
    final String TAG="InitialDetailActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_dialog_activity);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        proceed = (Button) findViewById(R.id.btn_proceed1);
        back = (Button) findViewById(R.id.btn_back);
        proceed.setOnClickListener(this);
        back.setOnClickListener(this);
        fragment_manager=getSupportFragmentManager();
        back.setVisibility(View.INVISIBLE);
        pref=getApplicationContext().getSharedPreferences("user_credentials",0);
        setupFirstInfoSlide();
        current_frag=1;
        addBottomDots(0);
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[3];
        int colorsActive = ResourcesCompat.getColor(getResources(), R.color.dot_active, null);
        int colorsInactive = ResourcesCompat.getColor(getResources(), R.color.dot_inactive, null);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // a certain method is not valid for versions below adroid nougat
                dots[i].setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_COMPACT));
            }
            else
            {
                dots[i].setText(Html.fromHtml("&#8226;"));
            }
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_proceed1:
                handleFragmentNavigation(FORWARD_PROCEED_CODE);
                break;
            case R.id.btn_back:
                handleFragmentNavigation(BACK_PROCEED_CODE);
                break;
        }
    }

    @Override
    public void onReceive(String Source, String action,Object attachment) {
        switch(Source){
            case "ContactDialogFragment":
                handleMailFromContactFragment(action,attachment);
                break;
            case "VehicleNameDialogFrag":
                handleMailFromVehicleFragment(action,attachment);
                break;
        }
    }

    /************************ Custom Functions **********************************/
    private void handleFragmentNavigation(int nav_direction)
    {
        if(nav_direction==FORWARD_PROCEED_CODE) {
            //Handle the forward fragment navigation
            switch (current_frag) {
                case 1:
                    setupSecondContactSlide();
                    break;
                case 2:
                    setupThirdVehicleSlide();
                    break;
                case 3:
                    Intent pvi = new Intent(this, Main.class);
                    startActivity(pvi);
                    updateProfileOnDb();
                    finish();
                    break;
            }
        }
        else{
            //Handle the backward fragment navigation
            switch (current_frag) {
                case 2:
                    setupFirstInfoSlide();
                    break;
                case 3:
                    setupSecondContactSlide();
                    break;
            }
        }
    }
    //To setup first slide in the information dialog
    private void setupFirstInfoSlide() {
        //update the current frag int
        current_frag=1;
        //Disable the back button
        back.setVisibility(View.INVISIBLE);
        FragmentTransaction fragmentTransaction=fragment_manager.beginTransaction().replace(R.id.detailDialogFragmentHolder,new FirstInfoFragment()).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        fragmentTransaction.commit();
        proceed.setText("Next");
        //Set the dots
        addBottomDots(current_frag-1);
    }
    //To setup second slide in the information dialog
    private void setupSecondContactSlide() {
        //update the current frag int
        current_frag=2;
        //Disable the back button
        back.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction=fragment_manager.beginTransaction().replace(R.id.detailDialogFragmentHolder,new SecondConatctFragment()).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        fragmentTransaction.commit();
        proceed.setText("Next");
        //Set the dots
        addBottomDots(current_frag-1);

    }
    //To setup third slide
    private void setupThirdVehicleSlide() {
        //update the current frag int
        current_frag=3;
        //Disable the back button
        back.setVisibility(View.VISIBLE);
        proceed.setText("Done");
        FragmentTransaction fragmentTransaction=fragment_manager.beginTransaction().replace(R.id.detailDialogFragmentHolder,new ThirdVehicleFragment()).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        fragmentTransaction.commit();
        //Set the dots
        addBottomDots(current_frag-1);
    }
    //Handle mail from contact fragment
    private void handleMailFromContactFragment(String action,Object attachment){
        switch(action){
            case "UpdateMobileNumber":
                contact=(String)attachment;
                Log.d(TAG,"Handling mail from contact fragment; Contact is: "+contact);
                break;
        }
    }
    //Handle the mail from the vehicle name fragment
    private void handleMailFromVehicleFragment(String action,Object attachment){
        switch (action){
            case "UpdateVehicleName":
                vehicle_name=(String)attachment;
                Log.d(TAG,"Handling mail from contact fragment; Vehicle Name is: "+vehicle_name);
                break;
        }
    }
    //Handle updating user profile on firebase after filling the details dialog activity
    private void updateProfileOnDb(){
        DatabaseReference m= FirebaseDatabase.getInstance().getReference().child("Users/"+pref.getString("Id","null"));
        m.child("contact_phone").setValue(contact).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Firebase conatct update error Error:" + e.toString());
            }
        });;
        m.child("vehicle").setValue(vehicle_name).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Firebase vehicle name update Error:" + e.toString());
            }
        });;


    }

}
