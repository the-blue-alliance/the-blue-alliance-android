package com.thebluealliance.androidclient.adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.mytba.MyFavoritesFragment;
import com.thebluealliance.androidclient.fragments.mytba.MySubscriptionsFragment;

public class MyTBAFragmentPagerAdapter extends FragmentPagerAdapter {

    private int mCount = 2;

    public MyTBAFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return MyFavoritesFragment.newInstance();
            case 1:
                return MySubscriptionsFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            default:
            case 0:
                return "Favorites";
            case 1:
                return "Subscriptions";
        }
    }

    @Override
    public int getCount() {
        return mCount;
    }
}
