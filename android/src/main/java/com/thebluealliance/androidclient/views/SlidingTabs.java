package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.util.AttributeSet;

import com.thebluealliance.androidclient.R;

/**
 * Created by Nathan on 10/25/2014.
 */
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
        this.setCustomTabView(R.layout.tab_title_view, R.id.tab_title_view);
        this.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                // Make the indicator white
                return 0xFFFFFFFF;
            }
        });
    }
}
