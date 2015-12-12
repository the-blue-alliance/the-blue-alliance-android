package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;

import javax.inject.Inject;

public class EventInfoContainerClickListener implements View.OnClickListener {

    ViewEventActivity mActivity;

    @Inject
    public EventInfoContainerClickListener(Context context) {
        if (!(context instanceof ViewEventActivity)) {
            throw new IllegalArgumentException("EventInfoContainerClickListener must be used" +
                    "with a ViewEventActivity");
        }
        mActivity = (ViewEventActivity) context;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.top_teams_container) {
            mActivity.scrollToTab(ViewEventFragmentPagerAdapter.TAB_RANKINGS);  // Rankings
        } else if (id == R.id.top_oprs_container) {
            mActivity.scrollToTab(ViewEventFragmentPagerAdapter.TAB_STATS);  // Stats
        }
    }
}
