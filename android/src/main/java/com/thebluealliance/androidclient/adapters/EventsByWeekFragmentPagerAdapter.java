package com.thebluealliance.androidclient.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.background.DownloadEventList;
import com.thebluealliance.androidclient.comparators.EventWeekLabelSortComparator;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Created by Nathan on 4/22/2014.
 */
public class EventsByWeekFragmentPagerAdapter extends FragmentPagerAdapter {

    private int mCount;
    private int mYear;
    private ArrayList<String> thisYearsWeekLabels;
    private String selectedTab;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;


    public EventsByWeekFragmentPagerAdapter(Context c, FragmentManager fm, int year, PagerSlidingTabStrip tabs, ViewPager pager) {
        super(fm);
        mYear = year;
        thisYearsWeekLabels = new ArrayList<>();
        selectedTab = String.format(EventHelper.REGIONAL_LABEL, 1);
        thisYearsWeekLabels.add(selectedTab);
        this.tabs = tabs;
        this.pager = pager;
        mCount = 1;
        DownloadEventList task = new DownloadEventList(c, this);
        task.execute(year);
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
        selectedTab = getPageTitle(pager.getCurrentItem()).toString();
        thisYearsWeekLabels = labels;
        mCount = thisYearsWeekLabels.size();
        pager.setAdapter(this);
        notifyDataSetChanged();
        pager.setCurrentItem(thisYearsWeekLabels.indexOf(selectedTab));
    }

    @Override
    public Fragment getItem(int position) {
        return EventListFragment.newInstance(mYear, position, null, getPageTitle(position).toString());
    }

    public ArrayList<String> getLabels() {
        return thisYearsWeekLabels;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        tabs.notifyDataSetChanged();
    }
}
