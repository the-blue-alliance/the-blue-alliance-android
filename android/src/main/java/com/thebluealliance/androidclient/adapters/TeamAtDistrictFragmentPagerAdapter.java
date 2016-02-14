package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.district.TeamAtDistrictBreakdownFragment;
import com.thebluealliance.androidclient.fragments.district.TeamAtDistrictSummaryFragment;

public class TeamAtDistrictFragmentPagerAdapter extends FragmentPagerAdapter {

    public final String[] TITLES = {"Summary", "Breakdown"};

    private String mDistrictKey, mTeamKey;

    public TeamAtDistrictFragmentPagerAdapter(FragmentManager fm, String teamKey, String districtKey) {
        super(fm);
        mTeamKey = teamKey;
        mDistrictKey = districtKey;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;
        switch (position) {
            default:
            case 0:
                f = TeamAtDistrictSummaryFragment.newInstance(mTeamKey, mDistrictKey);
                break;
            case 1:
                f = TeamAtDistrictBreakdownFragment.newInstance(mTeamKey, mDistrictKey);
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
