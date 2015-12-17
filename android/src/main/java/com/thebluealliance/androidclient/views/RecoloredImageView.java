package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.thebluealliance.androidclient.R;

public class RecoloredImageView extends ImageView {

    private int tintColor;

    public RecoloredImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RecoloredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RecoloredImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RecoloredImageView, defStyleAttr, 0);
        if (a == null) {
            return;
        }
        tintColor = a.getColor(R.styleable.RecoloredImageView_tintColor, context.getResources().getColor(R.color.black));
        a.recycle();

        setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
    }
}
