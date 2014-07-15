package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.thebluealliance.androidclient.Utilities;

/**
 * Created by Nathan on 7/4/2014.
 * <p/>
 * This class puts the expand/collapse indicators on the right side of the group headers instead of the left.
 * Besides that, it functions just as the normal ExpandableListView does.
 */
public class ExpandableListView extends android.widget.ExpandableListView {
    public ExpandableListView(Context context) {
        super(context);
    }

    public ExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void updateSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setIndicatorBoundsRelative(getWidth() - Utilities.getPixelsFromDp(getContext(), 40), getWidth() - Utilities.getPixelsFromDp(getContext(), 16));
        } else {
            setIndicatorBounds(getWidth() - Utilities.getPixelsFromDp(getContext(), 40), getWidth() - Utilities.getPixelsFromDp(getContext(), 16));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateSize();
    }
}
