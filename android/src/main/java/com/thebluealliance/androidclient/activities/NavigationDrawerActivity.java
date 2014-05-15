package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.widget.FrameLayout;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datatypes.NavDrawerItem;
import com.thebluealliance.androidclient.fragments.NavigationDrawerFragment;

/**
 * Created by Nathan on 5/15/2014.
 */
public abstract class NavigationDrawerActivity extends FragmentActivity implements  NavigationDrawerFragment.OnNavigationDrawerListener {

    /**
     * Saved instance state key representing the last select navigation drawer item
     */
    private static final String STATE_SELECTED_NAV_ID = "selected_navigation_drawer_position";

    private int mCurrentSelectedNavigationItemId = -1;

    private NavigationDrawerFragment mNavDrawerFragment;

    private FrameLayout mContentView;

    private boolean mOpenBehindCurrentActivity = true;

    private NavigationDrawerFragment.OnNavigationDrawerListener mDrawerClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_start);

        mNavDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        mNavDrawerFragment.setUp(R.id.navigation_drawer_fragment,
                (DrawerLayout) findViewById(R.id.nav_drawer_layout),
                true);

        mContentView = (FrameLayout) findViewById(R.id.container);
    }

    public void setOpenClicksBehindCurrentActivity(boolean open) {
        mOpenBehindCurrentActivity = open;
    }

    @Override
    public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, mContentView);
    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        // Don't reload the fragment if the user selects the tab we are currently on
        int id = item.getId();
        startActivity(StartActivity.newInstance(this, id));
    }
}
