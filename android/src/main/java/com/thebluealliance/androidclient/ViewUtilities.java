package com.thebluealliance.androidclient;

import android.view.View;
import android.view.ViewTreeObserver;

public final class ViewUtilities {

    private ViewUtilities() {
        // unused
    }

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
                obs.removeOnGlobalLayoutListener(this);
            }
        });
    }
}
