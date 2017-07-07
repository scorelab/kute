package com.scorelab.kute.kute.PrivateVehicles.App.Fragments.InitialDetailDialogFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 07/07/17.
 */

public class ThirdVehicleFragment extends Fragment {
    View v;

    public ThirdVehicleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.vehicle_name_detail_dialog_fragment,container,false);
        return v;
    }

}
