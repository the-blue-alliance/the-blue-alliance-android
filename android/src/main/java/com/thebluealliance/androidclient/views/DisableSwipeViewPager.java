package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Nathan on 5/25/2014.
 */
public class DisableSwipeViewPager extends ViewPager {

    private boolean mSwipeEnabled = true;

    public DisableSwipeViewPager(Context context) {
        super(context);
    }

    public DisableSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mSwipeEnabled) {
            return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mSwipeEnabled) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setSwipeEnabled(boolean enabled) {
        mSwipeEnabled = enabled;
    }

    /**
     * This method will advance the ViewPager to the next page if the ViewPager is not currently
     * on the last page.
     *
     * @return true if the ViewPager was able to advance to the next page, false if otherwise.
     */
    public boolean advanceToNextPage() {
        if (getCurrentItem() < getAdapter().getCount() - 1) {
            setCurrentItem(getCurrentItem() + 1);
            return true;
        }
        return false;
    }

    /**
     * This method will return the ViewPager to the previous page is the ViewPager is not currently
     * on the first page
     *
     * @return true if the ViewPager was able to return the the previous page, false if otherwise.
     */
    public boolean returnToPreviousPage() {
        if (getCurrentItem() > 0) {
            setCurrentItem(getCurrentItem() - 1);
            return true;
        }
        return false;
    }
}
