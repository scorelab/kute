package com.scorelab.kute.kute.PrivateVehicles.App.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scorelab.kute.kute.R;


public class InitialDetailDialogs extends AppCompatActivity implements View.OnClickListener {
    TextView[]dots;
    LinearLayout dotsLayout;
    Button proceed,back;
    int current_frag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_dialog_activity);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        proceed = (Button) findViewById(R.id.btn_proceed1);
        back = (Button) findViewById(R.id.btn_back);
        proceed.setOnClickListener(this);
        back.setOnClickListener(this);
        back.setVisibility(View.INVISIBLE);
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
                handleFragmentNavigation();
                break;
            case R.id.btn_back:
                handleFragmentNavigation();
                break;

        }
    }
    /************************ Custom Functions **********************************/
    private void handleFragmentNavigation()
    {
        //Handle the fragment navigation
        switch(current_frag)
        {

        }

    }

}
