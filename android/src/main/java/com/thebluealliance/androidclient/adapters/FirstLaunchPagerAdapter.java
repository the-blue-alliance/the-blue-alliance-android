package com.thebluealliance.androidclient.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.thebluealliance.androidclient.R;

public class FirstLaunchPagerAdapter extends PagerAdapter {

    private int mCount = 3;
    private Activity activity;

    public FirstLaunchPagerAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.welcome_page;
                break;
            case 1:
                resId = R.id.loading_page;
                break;
            case 2:
                resId = R.id.load_finished_page;
                break;
        }
        return activity.findViewById(resId);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Do nothing
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
