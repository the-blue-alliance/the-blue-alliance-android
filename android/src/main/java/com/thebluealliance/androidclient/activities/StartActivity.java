package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.widget.ArrayAdapter;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datatypes.NavDrawerItem;
import com.thebluealliance.androidclient.fragments.AllTeamsListFragment;
import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;
import com.thebluealliance.androidclient.fragments.InsightsFragment;
import com.thebluealliance.androidclient.fragments.NavigationDrawerFragment;
import com.thebluealliance.androidclient.interfaces.ActionBarSpinnerListener;

/**
 * File created by phil on 4/20/14.
 */
public class StartActivity extends FragmentActivity implements ActionBar.OnNavigationListener,
        NavigationDrawerFragment.OnNavigationDrawerListener {

    /**
     * Saved instance state key representing the last select navigation drawer item
     */
    private static final String STATE_SELECTED_NAV_ID = "selected_navigation_drawer_position";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_YEAR_SPINNER_POSITION = "selected_spinner_position";

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";

    private int mCurrentSelectedNavigationItemId = -1;
    private int mCurrentSelectedYearPosition = -1;

    private NavigationDrawerFragment mNavDrawerFragment;

    private String[] dropdownItems = new String[]{"2014", "2013", "2012"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        int initNavId = R.id.nav_item_events;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_SELECTED_NAV_ID)) {
                initNavId = savedInstanceState.getInt(STATE_SELECTED_NAV_ID);
            }

            if (savedInstanceState.containsKey(STATE_SELECTED_YEAR_SPINNER_POSITION) && getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST) {
                getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_YEAR_SPINNER_POSITION));
            }
        }

        mNavDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        mNavDrawerFragment.setUp(R.id.navigation_drawer_fragment,
                (DrawerLayout) findViewById(R.id.nav_drawer_layout));

        switchToModeForId(initNavId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensure that the correct navigation item is highlighted when returning to the StartActivity
        mNavDrawerFragment.setItemSelected(mCurrentSelectedNavigationItemId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_YEAR_SPINNER_POSITION,
                getActionBar().getSelectedNavigationIndex());
        outState.putInt(STATE_SELECTED_NAV_ID, mCurrentSelectedNavigationItemId);
    }

    private void switchToModeForId(int id) {
        Fragment fragment;
        switch (id) {
            default:
            case R.id.nav_item_events:
                fragment = new EventsByWeekFragment();
                break;
            case R.id.nav_item_teams:
                fragment = new AllTeamsListFragment();
                break;
            case R.id.nav_item_insights:
                fragment = new InsightsFragment();
                break;
            case R.id.nav_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return;
        }
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, MAIN_FRAGMENT_TAG).commit();
        // This must be done before we lose the drawer
        mCurrentSelectedNavigationItemId = id;
    }

    private void resetActionBar() {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // This will be triggered whenever the drawer opens or closes.
        if (!mNavDrawerFragment.isDrawerOpen()) {
            resetActionBar();

            switch (mCurrentSelectedNavigationItemId) {
                case R.id.nav_item_events:
                    setupActionBarForEvents();
                    break;
                case R.id.nav_item_teams:
                    getActionBar().setTitle("Teams");
                    break;
                case R.id.nav_item_insights:
                    getActionBar().setTitle("Insights");
                    break;
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void setupActionBarForEvents() {
        getActionBar().setDisplayShowTitleEnabled(false);

        ArrayAdapter<String> actionBarAdapter = new ArrayAdapter<>(getActionBar().getThemedContext(), R.layout.actionbar_spinner, R.id.year, dropdownItems);
        actionBarAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActionBar().setListNavigationCallbacks(actionBarAdapter, this);
        getActionBar().setSelectedNavigationItem(0); //TODO take this value from savedinstancestate
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        if (f instanceof ActionBarSpinnerListener) {
            ((ActionBarSpinnerListener) f).actionBarSpinnerSelected(position, dropdownItems[position]);
        }
        mCurrentSelectedYearPosition = position;
        return true;
    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        // Don't reload the fragment if the user selects the tab we are currently on
        int id = item.getId();
        if (id != mCurrentSelectedNavigationItemId) {
            switchToModeForId(id);
        }
    }
}
