package com.scorelab.kute.kute.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scorelab.kute.kute.Adapters.SignupSliderAdapter;
import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 30/03/17.
 */

public class SignUpSliderActivity extends AppCompatActivity implements View.OnClickListener {
    SignupSliderAdapter pager_adapter;
    ViewPager view_pager;
    TextView []dots;
    LinearLayout dotsLayout;
    Button proceed,back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_slider_activity);
        pager_adapter=new SignupSliderAdapter(getSupportFragmentManager());
        view_pager=(ViewPager)findViewById(R.id.signupfragmentholder);
        view_pager.setAdapter(pager_adapter);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        proceed = (Button) findViewById(R.id.btn_proceed1);
        back = (Button) findViewById(R.id.btn_back);
        proceed.setOnClickListener(this);
        back.setOnClickListener(this);
        back.setVisibility(View.INVISIBLE);
        addBottomDots(0);

        view_pager.setOnTouchListener(new View.OnTouchListener()
        {
            //a view pager was used so that it saves the instance states and its on scroll events can be managed
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[2];

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
    private int getItem(int i) {
        return view_pager.getCurrentItem() + i;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_proceed1:
                movePager(v.getId());
                break;
            case R.id.btn_back:
                movePager(v.getId());
                break;

        }
    }

    public void movePager(int id)
    {
        Log.d("current position",String.format("%d",view_pager.getCurrentItem()));
        if(view_pager.getCurrentItem()==0)
        {
            view_pager.setCurrentItem(1,true);
            proceed.setText(getString(R.string.register));
            back.setVisibility(View.VISIBLE);
            addBottomDots(1);
        }
        else {
            if (id == R.id.btn_back) {
                proceed.setText(R.string.next);
                back.setVisibility(View.INVISIBLE);
                view_pager.setCurrentItem(0);
                addBottomDots(0);
            }
            else
            {
                //Follow the registration process
            }

        }

    }
}
