package com.thebluealliance.androidclient.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.district.DistrictEventsFragment;
import com.thebluealliance.androidclient.fragments.district.DistrictRankingsFragment;

public class ViewDistrictFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final String[] TITLES = {"Events", "Rankings"};

    private String mDistrictKey;

    public ViewDistrictFragmentPagerAdapter(FragmentManager fm, String districtKey) {
        super(fm);
        mDistrictKey = districtKey;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;
        switch (position) {
            default:
            case 0:
                f = DistrictEventsFragment.newInstance(mDistrictKey);
                break;
            case 1:
                f = DistrictRankingsFragment.newInstance(mDistrictKey);
                break;
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
