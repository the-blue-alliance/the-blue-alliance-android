package com.thebluealliance.androidclient.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.EventsByWeekFragmentPagerAdapter;
import com.thebluealliance.androidclient.models.Event;

import java.util.Date;

public class EventsByWeekFragment extends Fragment {

    private int mYear;
    private static final String YEAR = "YEAR";

    public static EventsByWeekFragment newInstance(int year) {
        EventsByWeekFragment f = new EventsByWeekFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO make the default year update to the current year, not a constant one
        if (getArguments() != null) {
            mYear = getArguments().getInt(YEAR, 2014);
        }
        Log.d(Constants.LOG_TAG, "mYear: " + mYear);
    }

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_event_list_fragment_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.event_pager);
        final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.event_pager_tabs);
        final Context c = getActivity();
        final int mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        // Fade out the view, load the adapter, fade back in the view
        view.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mViewPager.setAdapter(new EventsByWeekFragmentPagerAdapter(c, getChildFragmentManager(), mYear));
                        tabs.setViewPager(mViewPager);
                        int currentWeek = Event.competitionWeek(new Date());
                        //set the currently selected tab to the current week or week 1
                        int week1Index = ((EventsByWeekFragmentPagerAdapter)mViewPager.getAdapter()).getLabels().indexOf(String.format(Event.REGIONAL_LABEL, 1));
                        mViewPager.setCurrentItem(currentWeek > 10 ? Math.min(mViewPager.getAdapter().getCount(), week1Index) : currentWeek);
                        /** Explanation for above line:
                         * If the current week is sufficiently past CMP (10 is a large enough number for this), then
                         * show week 1 (which is either index 1 or 2, which we'll get from the adapter by finding its label
                         * Else, we display the current week (TODO this needs to be checked to ensure it only does this in the current year)
                         */
                        view.animate()
                                .alpha(1f)
                                .setDuration(mShortAnimationDuration)
                                .setListener(null).start();
                    }
                }).start();
        return view;
    }
}
