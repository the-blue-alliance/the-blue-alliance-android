package com.thebluealliance.androidclient.adapters;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;

import com.thebluealliance.androidclient.R;

public class MyTBAOnboardingPagerAdapter extends PagerAdapter {

    private final int mCount;
    private final ViewGroup mView;

    public MyTBAOnboardingPagerAdapter(ViewGroup view) {
        mView = view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Starting with API 33, there is an additional page to request notification permissions
            mCount = 6;
        } else {
            mCount = 5;
        }
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public int getLoginPageId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return mCount - 2;
        } else {
            return mCount - 1;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public int getNotificationPermissionPageId() {
        return mCount - 1;
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
            case 5:
                resId = R.id.page_six;
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
