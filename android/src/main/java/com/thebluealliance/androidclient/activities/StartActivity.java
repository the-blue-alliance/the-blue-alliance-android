package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
public class StartActivity extends FragmentActivity implements AdapterView.OnItemClickListener, ActionBar.OnNavigationListener,
        NavigationDrawerFragment.OnNavigationDrawerListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_YEAR_SPINNER_POSITION = "selected_spinner_position";

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";

    private int mCurrentSelectedNavigationItemPosition = -1;
    private int mCurrentSelectedYearPosition = -1;

    private NavigationDrawerFragment mNavDrawerFragment;

    private String[] dropdownItems = new String[]{"2014", "2013", "2012"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (savedInstanceState != null) {
            // Restore needed stuff
            Fragment f = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
            if (f == null) {
                Log.d("onCreate", "creating new fragment");
                switchToModeForPosition(mCurrentSelectedNavigationItemPosition);
            } else {
                Log.d("onCreate", "old fragment retained");
                setupActionBarForPosition(mCurrentSelectedNavigationItemPosition);
            }
            if (savedInstanceState.containsKey(STATE_SELECTED_YEAR_SPINNER_POSITION) && getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST) {
                getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_YEAR_SPINNER_POSITION));
            }
        } else {
            // Default to events view
            switchToModeForPosition(0);
        }

        mNavDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        mNavDrawerFragment.setUp(R.id.navigation_drawer_fragment, (DrawerLayout) findViewById(R.id.nav_drawer_layout));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_YEAR_SPINNER_POSITION,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Don't reload the fragment if the user selects the tab we are currently on
        if (position == mCurrentSelectedNavigationItemPosition) {
            // TODO
           //  mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }
        switchToModeForPosition(position);
    }

    private void switchToModeForPosition(int position) {
        Fragment fragment;
        switch (position) {
            default:
            case 0: //events
                fragment = new EventsByWeekFragment();
                setupActionBarForEvents();
                break;
            case 1: //teams
                fragment = new AllTeamsListFragment();
                setupActionBarForTeams();
                break;
            case 2: //insights
                fragment = new InsightsFragment();
                setupActionBarForInsights();
                break;
            case 3:
                startActivity(new Intent(this, SettingsActivity.class));
                // If we don't manually set the checked item, Android will try to be helpful and set
                // the clicked item as checked. We don't want this behavior when we click on settings,
                // so we manually set it to check the current navigation item.
                // TODO
               // mDrawerList.setItemChecked(mCurrentSelectedNavigationItemPosition, true);
              //  mDrawerLayout.closeDrawer(mDrawerList);
                return;
        }
        // TODO
        //mDrawerList.setItemChecked(position, true);
        // This notifies the custom adapter that the TextView with id R.id.title should be bold
       // ((NavigationDrawerAdapter) mDrawerList.getAdapter()).setItemSelected(position);
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, MAIN_FRAGMENT_TAG).commit();
        // This must be done before we lose the drawer
        mCurrentSelectedNavigationItemPosition = position;

        // TODO
       // mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void resetActionBar() {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
    }

    private void setupActionBarForPosition(int position) {
        switch (position) {
            case 0:
                setupActionBarForEvents();
                return;
            case 1:
                setupActionBarForTeams();
                return;
            case 2:
                setupActionBarForInsights();
        }
    }

    private void setupActionBarForEvents() {
        resetActionBar();
        getActionBar().setDisplayShowTitleEnabled(false);

        ArrayAdapter<String> actionBarAdapter = new ArrayAdapter<>(getActionBar().getThemedContext(), R.layout.actionbar_spinner, R.id.year, dropdownItems);
        actionBarAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActionBar().setListNavigationCallbacks(actionBarAdapter, this);
        getActionBar().setSelectedNavigationItem(0); //TODO take this value from savedinstancestate
    }

    private void setupActionBarForTeams() {
        resetActionBar();
        getActionBar().setTitle("Teams");
    }

    private void setupActionBarForInsights() {
        resetActionBar();
        getActionBar().setTitle("Insights");
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

    }
}
