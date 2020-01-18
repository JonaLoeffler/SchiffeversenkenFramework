package com.jona.schiffeversenken.utility;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.ImageView;

/**
 * Created by Jona on 15.08.2016.
 */
public class MyColorFilter {

    public static void filter(Context context, ImageView view, int color, int drawableId) {

        Drawable mDrawable = ResourcesCompat.getDrawable(context.getResources(), drawableId, null);
        mDrawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_ATOP);
        view.setImageDrawable(mDrawable);
    }

}
