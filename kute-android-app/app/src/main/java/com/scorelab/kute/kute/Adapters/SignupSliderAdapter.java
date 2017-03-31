package com.scorelab.kute.kute.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.scorelab.kute.kute.Activity.FragmentUI.EmailSignup;
import com.scorelab.kute.kute.Activity.FragmentUI.PasswordSignup;

/**
 * Created by nipunarora on 30/03/17.
 */

public class SignupSliderAdapter extends FragmentStatePagerAdapter {

    public SignupSliderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:return new EmailSignup();
            case 1:return new PasswordSignup();
            default:return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
