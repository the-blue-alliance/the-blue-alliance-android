package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.comparators.EventWeekLabelSortComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Nathan on 4/22/2014.
 */
public class EventsByWeekFragmentPagerAdapter extends FragmentPagerAdapter {

    private int mCount;
    private int mYear;
    private ArrayList<String> thisYearsWeekLabels;

    public EventsByWeekFragmentPagerAdapter(Context c, FragmentManager fm, int year) {
        super(fm);
        mYear = year;
        thisYearsWeekLabels = new ArrayList<>();
        try {
            thisYearsWeekLabels.addAll(DataManager.getEventsByYear(c, year).getData().keySet());
            Collections.sort(thisYearsWeekLabels, new EventWeekLabelSortComparator());
            mCount = thisYearsWeekLabels.size();
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
        }


    }

    @Override
    public CharSequence getPageTitle(int position) {
        Date now = new Date();
        if(Event.competitionWeek(now) == (position)){
            return "Current Week";
        }else {
            return thisYearsWeekLabels.get(position);
        }
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        return EventListFragment.newInstance(mYear, (position == 8?9:position), null);
        /**
         * Not sure of a better way to do this ATM, but the gap week between the last event and CMP is throwing things off
         * I don't think it'll be affected by prior years' schedules, but I can fix that when it comes
         */
    }
}
