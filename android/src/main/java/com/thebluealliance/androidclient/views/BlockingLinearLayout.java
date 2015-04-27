package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Nathan on 4/18/2015.
 *
 * Blocks focus/activated/selected/etc state from propagating down to children.
 */
public class BlockingLinearLayout extends LinearLayout {
    public BlockingLinearLayout(Context context) {
        super(context);
    }

    public BlockingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
