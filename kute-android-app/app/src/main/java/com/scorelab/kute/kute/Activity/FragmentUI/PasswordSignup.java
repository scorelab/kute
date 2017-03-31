package com.scorelab.kute.kute.Activity.FragmentUI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 30/03/17.
 */

public class PasswordSignup extends Fragment {
    View rootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview=inflater.inflate(R.layout.sign_up_password_fragment,container,false);
        return rootview;
    }
}
