package com.thebluealliance.androidclient.adapters;

import com.thebluealliance.androidclient.fragments.event.EventAlliancesFragment;
import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventDistrictPointsFragment;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.fragments.event.EventMatchesFragment;
import com.thebluealliance.androidclient.fragments.event.EventRankingsFragment;
import com.thebluealliance.androidclient.fragments.event.EventStatsFragment;
import com.thebluealliance.androidclient.fragments.event.EventTeamsFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ViewEventFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<WeakReference<Fragment>> mFragments = new ArrayList<>();
    private FragmentManager mFragmentManager;

    public static final String[] TITLES = {"Info", "Teams", "Rankings", "Matches",
            "Alliances", "District Points", "Stats", "Awards"};
    public static final int
            TAB_INFO = 0,
            TAB_TEAMS = 1,
            TAB_RANKINGS = 2,
            TAB_MATCHES = 3,
            TAB_ALLIANCES = 4,
            TAB_DISTRICT_POINTS = 5,
            TAB_STATS = 6,
            TAB_AWARDS = 7;

    private String mEventKey;

    public ViewEventFragmentPagerAdapter(FragmentManager fm, String eventKey) {
        super(fm);
        mFragmentManager = fm;
        mEventKey = eventKey;
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
        Fragment fragment;
        switch (position) {
            /*
            case TAB_TICKER:
                fragment = EventTickerFragment.newInstance(mEventKey);
                break;
            */
            case TAB_INFO:
                fragment = EventInfoFragment.newInstance(mEventKey);
                break;
            case TAB_TEAMS:
                fragment = EventTeamsFragment.newInstance(mEventKey);
                break;
            case TAB_RANKINGS:
                fragment = EventRankingsFragment.newInstance(mEventKey);
                break;
            case TAB_MATCHES:
                fragment = EventMatchesFragment.newInstance(mEventKey);
                break;
            case TAB_ALLIANCES:
                fragment = EventAlliancesFragment.newInstance(mEventKey);
                break;
            case TAB_DISTRICT_POINTS:
                fragment = EventDistrictPointsFragment.newInstance(mEventKey);
                break;
            case TAB_STATS:
                fragment = EventStatsFragment.newInstance(mEventKey);
                break;
            case TAB_AWARDS:
                fragment = EventAwardsFragment.newInstance(mEventKey);
                break;
            default:
                fragment = new Fragment();
        }
        mFragments.add(new WeakReference<>(fragment));
        return fragment;
    }

    public void removeAllFragments() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        for (WeakReference<Fragment> reference : mFragments) {
            Fragment f = reference.get();
            if (f != null) {
                transaction.remove(f);
            }
        }
        transaction.commit();
    }
}
