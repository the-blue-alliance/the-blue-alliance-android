package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.background.DownloadEventList;
import com.thebluealliance.androidclient.fragments.EventListFragment;
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
    private ArrayList<EventListFragment> fragments;
    private String selectedTab;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private boolean tabsChanged;


    public EventsByWeekFragmentPagerAdapter(Context c, FragmentManager fm, int year, PagerSlidingTabStrip tabs, ViewPager pager) {
        super(fm);
        mYear = year;
        thisYearsWeekLabels = new ArrayList<>();
        fragments = new ArrayList<>();
        selectedTab = String.format(EventHelper.REGIONAL_LABEL, 1);
        thisYearsWeekLabels.add(selectedTab);
        this.tabs = tabs;
        this.pager = pager;
        mCount = 1;
        DownloadEventList task = new DownloadEventList(c, this);
        task.execute(year);
        tabsChanged = false;
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

    public void setLabels(ArrayList<String> labels){
        tabsChanged = labels.size() != thisYearsWeekLabels.size() || !thisYearsWeekLabels.equals(labels);
        selectedTab = getPageTitle(pager.getCurrentItem()).toString();
        thisYearsWeekLabels = labels;
        mCount = thisYearsWeekLabels.size();
        pager.setAdapter(this);
        notifyDataSetChanged();
        pager.setCurrentItem(thisYearsWeekLabels.indexOf(selectedTab));
    }

    @Override
    public Fragment getItem(int position) {
        EventListFragment f = EventListFragment.newInstance(mYear, position, null, getPageTitle(position).toString());
        fragments.add(position, f);
        return f;
    }

    public ArrayList<String> getLabels() {
        return thisYearsWeekLabels;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        tabs.notifyDataSetChanged();
        for (int i = 0; i < fragments.size(); i++) {
            fragments.get(i).updateHeader(getPageTitle(i).toString());
        }
    }
}
