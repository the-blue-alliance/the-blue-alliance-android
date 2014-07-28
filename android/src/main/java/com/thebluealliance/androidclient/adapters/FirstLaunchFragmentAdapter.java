package com.thebluealliance.androidclient.adapters;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;

/**
 * Created by Nathan on 4/22/2014.
 */
public class FirstLaunchFragmentAdapter extends PagerAdapter {

    private int mCount = 4;
    private Activity activity;

    public FirstLaunchFragmentAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return mCount;
    }

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
                resId = R.id.account_picker_page;
                break;
            case 3:
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
