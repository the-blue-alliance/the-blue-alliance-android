package com.thebluealliance.androidclient;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Nathan on 5/20/2014.
 */
public class Utilities {

    public static int getPixelsFromDp(Context c, int dipValue){
        Resources r = c.getResources();
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
                r.getDisplayMetrics());
        return px;
    }
}
