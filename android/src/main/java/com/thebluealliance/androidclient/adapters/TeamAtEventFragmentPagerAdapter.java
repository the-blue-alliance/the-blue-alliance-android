package com.thebluealliance.androidclient.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventMatchesFragment;
import com.thebluealliance.androidclient.fragments.team.TeamMediaFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventStatsFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventSummaryFragment;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.interfaces.HasEventParam;

import java.util.List;

public class TeamAtEventFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final String[] TITLES = {"Summary", "Matches", "Media", "Stats", "Awards"};
    public static final int TAB_SUMMARY = 0,
            TAB_MATCHES = 1,
            TAB_MEDIA = 2,
            TAB_STATS = 3,
            TAB_AWARDS = 4;

    private String mTeamKey, mEventKey;
    private FragmentManager mFragmentManager;

    public TeamAtEventFragmentPagerAdapter(FragmentManager fm, String teamKey, String eventKey) {
        super(fm);
        mTeamKey = teamKey;
        mEventKey = eventKey;
        mFragmentManager = fm;
    }

    public void updateEvent(String eventKey) {
        if (!mEventKey.equals(eventKey)) {
            List<Fragment> fragments = mFragmentManager.getFragments();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            for (Fragment f : fragments) {
                //You can perform additional check to remove some (not all) fragments:
                if (getItemPosition(f) == POSITION_NONE) {
                    ft.remove(f);
                }
            }
            ft.commitAllowingStateLoss();
        }
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
        switch (position) {
            case TAB_SUMMARY:
                return TeamAtEventSummaryFragment.newInstance(mTeamKey, mEventKey);
            case TAB_MATCHES:
                return EventMatchesFragment.newInstance(mEventKey, mTeamKey);
            case TAB_MEDIA:
                return TeamMediaFragment.newInstance(mTeamKey, EventHelper.getYear(mEventKey));
            case TAB_STATS:
                return TeamAtEventStatsFragment.newInstance(mTeamKey, mEventKey);
            case TAB_AWARDS:
                return EventAwardsFragment.newInstance(mEventKey, mTeamKey);
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof HasEventParam) {
            HasEventParam fragment = (HasEventParam) object;
            String eventKey = fragment.getEventKey();
            if (eventKey.isEmpty()) {
                return POSITION_UNCHANGED;
            } else if (!eventKey.equals(mEventKey)) {
                return POSITION_NONE;
            }
        }
        return super.getItemPosition(object);
    }

    @Override
    public long getItemId(int position) {
        return (mEventKey + "_" + position).hashCode();
    }
}
