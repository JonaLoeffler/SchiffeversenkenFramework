package com.jona.schiffeversenken.gameobjects;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import framework.core.GameObject;

public abstract class Ship extends GameObject {

    private static final String TAG = "Ship";
    private int orientation;
    private float lastRotation;
    public boolean turnable = false;

    public Ship(Context context) {
        super(context);
        init();
    }

    public Ship(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Ship(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(BattleShipsGameBoard.LayoutParams.ORIENTATION_HORIZONTAL);

    }

    /**
     * Rotates the View. Always performs the rotation by the fewer degrees
     * <p/>
     * //     * @param fromDegrees The fromDegrees parameter of the RotateAnimation.
     * //     * @param toDegrees   The toDegrees parameter of the RotateAnimation.
     *
     * @param duration The time duration in milliseconds.
     */
    public void setRotationWithAnimation(float degrees, long duration) {
        Log.d(TAG, "setRotationWithAnimation start: rotation " + getRotation());
        lastRotation = degrees;

        RotateAnimation animation = new RotateAnimation(0, degrees, getPivotX(), getPivotY());
        animation.setDuration(duration);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (lastRotation == 90) setRotation(0);
                else setRotation(-90);

                clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (getAnimation() == null)
            startAnimation(animation);

        Log.d(TAG, "setRotationWithAnimation end: rotation " + getRotation());
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setOrientation(int orientation) throws IllegalArgumentException {
        if (orientation == BattleShipsGameBoard.LayoutParams.ORIENTATION_HORIZONTAL || orientation == BattleShipsGameBoard.LayoutParams.ORIENTATION_VERTICAL)
            this.orientation = orientation;
        else {
            IllegalArgumentException e = new IllegalArgumentException("Illegal Orientation");
            throw e;
        }
    }

    public abstract int getShipLength();

    /**
     * setzen ob schiff ausgewaehlt/im drag ist
     */
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }
}