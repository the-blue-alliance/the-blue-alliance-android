package com.thebluealliance.androidclient.adapters;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.fragments.TeamListFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TeamListFragmentPagerAdapter extends FragmentStateAdapter {

    private static final int TEAMS_PER_TAB = 1000;

    private int pageCount;

    public TeamListFragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setMaxTeamNumber(int maxTeamNumber) {
        pageCount = (maxTeamNumber / TEAMS_PER_TAB) + 1;
        TbaLogger.d("LARGEST TEAM: " + maxTeamNumber);
        TbaLogger.d("USING " + pageCount + " PAGES");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TeamListFragment.newInstance(position * 1000);
    }

    @Override
    public int getItemCount() {
        return pageCount;
    }
}
