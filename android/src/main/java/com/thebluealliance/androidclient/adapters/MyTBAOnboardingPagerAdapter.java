package com.thebluealliance.androidclient.adapters;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;

public class MyTBAOnboardingPagerAdapter extends PagerAdapter {

    private int mCount = 5;
    private ViewGroup mView;

    public MyTBAOnboardingPagerAdapter(ViewGroup view) {
        mView = view;
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
                resId = R.id.page_one;
                break;
            case 1:
                resId = R.id.page_two;
                break;
            case 2:
                resId = R.id.page_three;
                break;
            case 3:
                resId = R.id.page_four;
                break;
            case 4:
                resId = R.id.page_five;
                break;
        }
        return mView.findViewById(resId);
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
