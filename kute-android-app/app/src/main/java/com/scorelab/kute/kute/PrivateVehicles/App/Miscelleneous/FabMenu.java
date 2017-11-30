package com.scorelab.kute.kute.PrivateVehicles.App.Miscelleneous;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scorelab.kute.kute.R;

/**
 * Created by nipunarora on 20/04/17.
 */

public class FabMenu {
    int buttonheight,buttonwidth;
    int ANIMATION_DURATION = 300; //time for which the animation will be played
    int startPositionX = 0;
    int startPositionY = 0;
    int[] enterDelay = {80, 120};
    int[] exitDelay = {80, 40};
    int whichAnimation = 0; //variable to determine whether entry animation is to be played or exit animation
    Point[] FabmenuButtonVertices;
    Context mContext;
    int windowheight,windowwidth;

    public FabMenu(Context context,int height,int width) {
        mContext=context;
        windowheight=height;
        windowwidth=width;
        buttonheight = (int) mContext.getResources().getDimension(R.dimen.button_height);
        buttonwidth = (int) mContext.getResources().getDimension(R.dimen.button_width);
        calculateFABMenuVertices(windowheight,windowwidth);

    }
    public void setXYStartPosition(int x,int y)
    {
        startPositionY=y;
        startPositionX=x;
    }
    private void calculateFABMenuVertices(int window_height,int window_width) {
        int centerY = window_height / 2;
        int centerX = window_width / 2;
        FabmenuButtonVertices=new Point[2];
        FabmenuButtonVertices[0]=new Point(centerX-centerX/2,centerY-150);
        FabmenuButtonVertices[1]=new Point(centerX+centerX/2-20,centerY-150);
    }
    public void playEnterAnimation(final Button button, int position, final TextView textView,final View scroll_view ) {

        AnimatorSet buttonAnimator = new AnimatorSet();

        //these fractional addition and subtractions have been made to balance the offset due to the size as the
        //the button centre would be assigned to the co ordinate and we need to subtract half its width to balance it

        //******************** CREATING THE X ANIMATOR **************/
        ValueAnimator buttonAnimatorX = ValueAnimator.ofFloat(startPositionX + button.getLayoutParams().width / 2,
                FabmenuButtonVertices[position].x);

        buttonAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                button.setX((float) animation.getAnimatedValue() - button.getLayoutParams().width / 2);
                button.requestLayout();
                textView.setX((float) animation.getAnimatedValue() - (button.getLayoutParams().width / 2));
                textView.requestLayout();
            }
        });
        buttonAnimatorX.setDuration(ANIMATION_DURATION);

        //*************************** CREATING THE Y ANIMATOR *******************/
        ValueAnimator buttonAnimatorY = ValueAnimator.ofFloat(startPositionY + 5,
                FabmenuButtonVertices[position].y);
        buttonAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                button.setY((float) animation.getAnimatedValue());
                button.requestLayout();
                textView.setY((float) animation.getAnimatedValue()+(button.getLayoutParams().height)+20);
                textView.requestLayout();

            }

        });
        buttonAnimatorY.setDuration(ANIMATION_DURATION);


        //*********************** CREATING THE SIZE ANIMATOR ********************??
        ValueAnimator buttonSizeAnimator = ValueAnimator.ofInt(5, buttonwidth); //initially we have the params to 5,5 while creating the buttons now we are animating
        //upto the value given in the dimens file
        buttonSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                button.getLayoutParams().width = (int) animation.getAnimatedValue();
                button.getLayoutParams().height = (int) animation.getAnimatedValue();
                button.requestLayout();
                textView.getLayoutParams().width = (int) animation.getAnimatedValue()+buttonwidth;
                textView.getLayoutParams().height = (int) animation.getAnimatedValue();
                textView.requestLayout();
            }
        });
        buttonSizeAnimator.setDuration(ANIMATION_DURATION);


        buttonAnimator.play(buttonAnimatorX).with(buttonAnimatorY).with(buttonSizeAnimator);
        buttonAnimator.setStartDelay(enterDelay[position]);
        buttonAnimator.start();
        buttonAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //Fade the background View
                scroll_view.setAlpha((float)0.05);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
    public void playExitAnimation(final Button button, int position, final TextView textView, final View scroll_view) {


        AnimatorSet buttonAnimator = new AnimatorSet();

        ValueAnimator buttonAnimatorX = ValueAnimator.ofFloat(FabmenuButtonVertices[position].x - button.getLayoutParams().width / 2,
                startPositionX);
        buttonAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                button.setX((float) animation.getAnimatedValue());
                button.requestLayout();
                textView.setX((float) animation.getAnimatedValue()-40);
                textView.requestLayout();

            }
        });
        buttonAnimatorX.setDuration(ANIMATION_DURATION);

        ValueAnimator buttonAnimatorY = ValueAnimator.ofFloat(FabmenuButtonVertices[position].y,
                startPositionY + 5);
        buttonAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                button.setY((float) animation.getAnimatedValue());
                button.requestLayout();
                textView.setY((float) animation.getAnimatedValue()+((button.getLayoutParams().height)+20));
                textView.requestLayout();
            }
        });
        buttonAnimatorY.setDuration(ANIMATION_DURATION);
        ValueAnimator buttonSizeAnimator = ValueAnimator.ofInt(buttonwidth, 5);
        buttonSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                button.getLayoutParams().width = (int) animation.getAnimatedValue();
                button.getLayoutParams().height = (int) animation.getAnimatedValue();
                button.requestLayout();
                textView.getLayoutParams().width = (int) animation.getAnimatedValue()+buttonwidth;
                textView.getLayoutParams().height = (int) animation.getAnimatedValue();
                textView.requestLayout();
            }
        });
        buttonSizeAnimator.setDuration(ANIMATION_DURATION);

        buttonAnimator.play(buttonAnimatorX).with(buttonAnimatorY).with(buttonSizeAnimator);
        buttonAnimator.setStartDelay(exitDelay[position]);
        buttonAnimator.start();
        buttonAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //Un Fade the background View
                scroll_view.setAlpha(1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

}
