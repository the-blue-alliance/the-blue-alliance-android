package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Nathan on 8/14/2014.
 *
 * An ImageView with an optional foreground drawable.
 */
public class ImageViewWithForeground extends ImageView {

    private Drawable mForegroundSelector;

    public ImageViewWithForeground(Context context) {
        super(context);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if(mForegroundSelector != null) {
            mForegroundSelector.setState(getDrawableState());
        }

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(mForegroundSelector != null) {
            mForegroundSelector.setBounds(0, 0, w, h);
        }
    }

    public void setForeground(Drawable drawable) {
        mForegroundSelector = drawable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mForegroundSelector.draw(canvas);
    }
}
