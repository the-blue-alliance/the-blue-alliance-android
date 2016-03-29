package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.fragments.team.TeamEventsFragment;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.fragments.team.TeamMediaFragment;
import com.thebluealliance.androidclient.interfaces.HasYearParam;

public class ViewTeamFragmentPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Info", "Events", "Media"};
    public static final int TAB_INFO = 0,
            TAB_EVENTS = 1,
            TAB_MEDIA = 2;

    private String mTeamKey;
    private int mYear;

    public ViewTeamFragmentPagerAdapter(FragmentManager fm, String teamKey, int year) {
        super(fm);
        mTeamKey = teamKey;
        mYear = year;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_INFO:
                return TeamInfoFragment.newInstance(mTeamKey);
            case TAB_EVENTS:
                return TeamEventsFragment.newInstance(mTeamKey, mYear);
            case TAB_MEDIA:
                return TeamMediaFragment.newInstance(mTeamKey, mYear);
            default:
                return new Fragment();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    public void updateYear(int year) {
        mYear = year;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof HasYearParam) {
            HasYearParam fragment = (HasYearParam) object;
            if (fragment.getYear() != mYear) {
                return POSITION_NONE;
            }
        }
        return super.getItemPosition(object);
    }

    @Override
    public long getItemId(int position) {
        return mYear * (position + 1);
    }
}
