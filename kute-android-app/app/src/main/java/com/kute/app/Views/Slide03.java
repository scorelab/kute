package com.kute.app.Views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kute.app.R;

/**
 * Created by Deniz on 29.12.2016.
 */

public class Slide03 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro3, container, false);
        return v;
    }
}
