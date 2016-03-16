package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Blocks focus/activated/selected/etc state from propagating down to children.
 */
public class BlockingRelativeLayout extends RelativeLayout {
    public BlockingRelativeLayout(Context context) {
        super(context);
    }

    public BlockingRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockingRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        return;
    }

    @Override
    public void dispatchSetActivated(boolean activated) {
        return;
    }

    @Override
    public void dispatchSetSelected(boolean selected) {
        return;
    }
}
