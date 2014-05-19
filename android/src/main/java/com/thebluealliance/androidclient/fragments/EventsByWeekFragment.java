package com.thebluealliance.androidclient.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.EventsByWeekFragmentPagerAdapter;
import com.thebluealliance.androidclient.interfaces.ActionBarSpinnerListener;

public class EventsByWeekFragment extends Fragment implements ActionBarSpinnerListener {

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
        if (savedInstanceState != null && savedInstanceState.containsKey(YEAR)) {
            mYear = savedInstanceState.getInt(YEAR);
        }
    }

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(YEAR)) {
            mYear = savedInstanceState.getInt(YEAR);
        }
        return inflater.inflate(R.layout.fragment_event_list_fragment_pager, container, false);
    }

    @Override
    public void actionBarSpinnerSelected(int position, String yearString) {
        int year = Integer.parseInt(yearString);
        // Only update the view when the year changes
        if (year == mYear) {
            return;
        }
        mYear = year;
        final View view = getView();
        mViewPager = (ViewPager) view.findViewById(R.id.event_pager);
        final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.event_pager_tabs);
        final int mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        // Fade out the view, load the adapter, fade back in the view
        view.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mViewPager.setAdapter(new EventsByWeekFragmentPagerAdapter(getChildFragmentManager(), mYear));
                        tabs.setViewPager(mViewPager);
                        view.animate()
                                .alpha(1f)
                                .setDuration(mShortAnimationDuration)
                                .setListener(null).start();
                    }
                }).start();
    }
}
