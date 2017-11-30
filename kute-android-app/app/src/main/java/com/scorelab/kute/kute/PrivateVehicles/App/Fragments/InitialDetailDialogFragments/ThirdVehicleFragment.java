package com.scorelab.kute.kute.PrivateVehicles.App.Fragments.InitialDetailDialogFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.FragmentToActivityMail;
import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 07/07/17.
 */

public class ThirdVehicleFragment extends Fragment {
    final String TAG="VehicleNameDialogFrag";
    View v;
    EditText input_vehicle_name;
    final String VEHICLE_UPDATE_ACTION="UpdateVehicleName";
    FragmentToActivityMail mailer_to_activity;

    public ThirdVehicleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.vehicle_name_detail_dialog_fragment,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        input_vehicle_name=(EditText)v.findViewById(R.id.input_vehicle_name);
        input_vehicle_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mailer_to_activity.onReceive(TAG,VEHICLE_UPDATE_ACTION,input_vehicle_name.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mailer_to_activity = (FragmentToActivityMail) context;
        } catch (Exception e) {
            Log.d(TAG, "Error attaching mailer to activity" + e.toString());
        }
    }
}
