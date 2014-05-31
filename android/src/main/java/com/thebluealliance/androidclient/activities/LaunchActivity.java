package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.FirstLaunchFragmentAdapter;
import com.thebluealliance.androidclient.views.DisableSwipeViewPager;

/**
 * Created by Nathan on 5/25/2014.
 */
public class LaunchActivity extends FragmentActivity {

    private static final String ALL_DATA_LOADED = "all_data_loaded";

    private DisableSwipeViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ALL_DATA_LOADED, false) == true) {
            startActivity(new Intent(this, StartActivity.class));
            return;
        }
        setContentView(R.layout.activity_first_launch);
        viewPager = (DisableSwipeViewPager) findViewById(R.id.view_pager);
        viewPager.setSwipeEnabled(false);
        viewPager.setAdapter(new FirstLaunchFragmentAdapter(getSupportFragmentManager()));
    }

    public void advanceToNextPage() {
        if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    public void returnToPreviousPage() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}
