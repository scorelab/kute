package com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 25/06/17.
 */
//This is a place holder fragment created to placed at positions where content is not curetly availabe
//Such as a Fragment where we need to add a loading label to inform the user the content is currently being loaded
public class PlaceHolderFragment extends Fragment {
    View v;
    TextView place_holder_label;

    public PlaceHolderFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.placeholder_fragment, container, false);
        place_holder_label = (TextView) v.findViewById(R.id.placeHolderLabel);
        if (getArguments() != null) {
            place_holder_label.setText(getArguments().getString("Label"));
        }
        return v;
    }
}
