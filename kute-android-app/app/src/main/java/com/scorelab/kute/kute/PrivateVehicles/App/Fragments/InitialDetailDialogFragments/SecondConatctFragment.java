package com.scorelab.kute.kute.PrivateVehicles.App.Fragments.InitialDetailDialogFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.FragmentToActivityMail;
import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 07/07/17.
 */

public class SecondConatctFragment extends Fragment {
    View v;
    EditText input_mobile;
    CoordinatorLayout activity_coordinator;
    final String TAG="ContactDialogFragment";
    final String UPDATE_CONTACT_ACTION="UpdateMobileNumber";
    FragmentToActivityMail mailer_to_activity;
    TextWatcher textWatcher;
    Button btn_next;
    public SecondConatctFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.contactdetail_detail_dialog_fragment,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        input_mobile=(EditText)v.findViewById(R.id.input_mobile);
        activity_coordinator=(CoordinatorLayout)getActivity().findViewById(R.id.detailsDialogCoordinator);
        btn_next=(Button)getActivity().findViewById(R.id.btn_proceed1);
        btn_next.setVisibility(View.INVISIBLE);
        textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG,"Length of the phone number"+Integer.toString(input_mobile.getText().length()));
                if(input_mobile.getText().length()==10){
                    btn_next.setVisibility(View.VISIBLE);
                }
                else if(input_mobile.getText().length()==9){
                    btn_next.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        input_mobile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if(input_mobile.getText().length()!=10)//check if mobile number is valid or not
                    {
                        Snackbar.make(activity_coordinator,"Enter A Valid Contact Number to proceed",Snackbar.LENGTH_SHORT).show();

                    }
                    else {
                        mailer_to_activity.onReceive(TAG, UPDATE_CONTACT_ACTION, input_mobile.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
        input_mobile.addTextChangedListener(textWatcher);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mailer_to_activity=(FragmentToActivityMail) context;
        }catch(Exception e){
            Log.d(TAG,"Error attaching mailer to activity"+e.toString());
        }
    }
}
