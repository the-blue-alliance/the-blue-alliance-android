package com.thebluealliance.androidclient;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

public class ViewUtilities {
    public static void runOnceAfterLayout(View view, Runnable run) {
        if (run == null) {
            return;
        }

        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                run.run();

                ViewTreeObserver obs = view.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }
}
