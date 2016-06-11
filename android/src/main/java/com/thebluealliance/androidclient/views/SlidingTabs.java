package com.thebluealliance.androidclient.views;

import com.thebluealliance.androidclient.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class SlidingTabs extends SlidingTabLayout {
    public SlidingTabs(Context context) {
        super(context);
        init();
    }

    public SlidingTabs(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingTabs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        this.setSelectedIndicatorColors(Color.WHITE);
    }
}
