package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.fragments.RefreshableHostFragment;
import com.thebluealliance.androidclient.helpers.EventHelper;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nathan on 4/22/2014.
 */
public class EventsByWeekFragmentPagerAdapter extends FragmentPagerAdapter{

    private int mCount;
    private int mYear;
    private ArrayList<String> thisYearsWeekLabels;
    private PagerSlidingTabStrip tabs;
    private RefreshableHostFragment parent;


    public EventsByWeekFragmentPagerAdapter(RefreshableHostFragment fragment, FragmentManager fm, int year, PagerSlidingTabStrip tabs, ViewPager pager, ArrayList<String> labels){
        this(fragment, fm, year, tabs, pager);
        thisYearsWeekLabels = labels;
        mCount = labels.size();
    }

    public EventsByWeekFragmentPagerAdapter(RefreshableHostFragment fragment, FragmentManager fm, int year, PagerSlidingTabStrip tabs, ViewPager pager) {
        super(fm);
        mYear = year;
        thisYearsWeekLabels = new ArrayList<>();
        thisYearsWeekLabels.add("");
        this.tabs = tabs;
        this.parent = fragment;
        mCount = 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Date now = new Date();
        if (EventHelper.competitionWeek(now) == position && Utilities.getCurrentYear() == mYear) {
            return "Current Week";
        } else {
            return thisYearsWeekLabels.get(position);
        }
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        EventListFragment f = EventListFragment.newInstance(mYear, position, null, getPageTitle(position).toString());
        f.setHost(parent);
        return f;
    }

    public ArrayList<String> getLabels() {
        return thisYearsWeekLabels;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        tabs.notifyDataSetChanged();
    }
}
