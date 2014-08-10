package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.settings.SettingsActivity;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.fragments.AllTeamsListFragment;
import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;
import com.thebluealliance.androidclient.fragments.InsightsFragment;
import com.thebluealliance.androidclient.fragments.district.DistrictListFragment;
import com.thebluealliance.androidclient.fragments.mytba.MyTBAFragment;
import com.thebluealliance.androidclient.listitems.NavDrawerItem;

import java.util.Calendar;

/**
 * File created by phil on 4/20/14.
 */
public class HomeActivity extends RefreshableHostActivity implements ActionBar.OnNavigationListener {

    /**
     * Saved instance state key representing the last select navigation drawer item
     */
    private static final String STATE_SELECTED_NAV_ID = "selected_navigation_drawer_position";

    private static final String REQUESTED_MODE = "requested_mode";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_YEAR_SPINNER_POSITION = "selected_spinner_position";

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";

    private boolean fromSavedInstance = false;

    private int mCurrentSelectedNavigationItemId = -1;
    private int mCurrentSelectedYearPosition = -1;

    private String[] eventsDropdownItems, districtsDropdownItems;

    private TextView warningMessage;

    public static Intent newInstance(Context context, int requestedMode) {
        Intent i = new Intent(context, HomeActivity.class);
        i.putExtra(REQUESTED_MODE, requestedMode);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        eventsDropdownItems = new String[Constants.MAX_COMP_YEAR - Constants.FIRST_COMP_YEAR + 1];
        for (int i = 0; i < eventsDropdownItems.length; i++) {
            eventsDropdownItems[i] = Integer.toString(Constants.MAX_COMP_YEAR - i);
        }

        districtsDropdownItems = new String[Constants.MAX_COMP_YEAR - Constants.FIRST_DISTRICT_YEAR + 1];
        for (int i = 0; i < districtsDropdownItems.length; i++) {
            districtsDropdownItems[i] = Integer.toString(Constants.MAX_COMP_YEAR - i);
        }

        int initNavId = R.id.nav_item_events;
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey(REQUESTED_MODE)) {
                if (b.getInt(REQUESTED_MODE, -1) != -1) {
                    initNavId = b.getInt(REQUESTED_MODE);
                }
            }
        }

        if (savedInstanceState != null) {
            fromSavedInstance = true;
            Log.d(Constants.LOG_TAG, "StartActivity is from saved instance");
            if (savedInstanceState.containsKey(STATE_SELECTED_NAV_ID)) {
                mCurrentSelectedNavigationItemId = savedInstanceState.getInt(STATE_SELECTED_NAV_ID);
            }

            if (savedInstanceState.containsKey(STATE_SELECTED_YEAR_SPINNER_POSITION)) {
                mCurrentSelectedYearPosition = savedInstanceState.getInt(STATE_SELECTED_YEAR_SPINNER_POSITION);
            }
        } else {
            if (Calendar.getInstance().get(Calendar.YEAR) == Constants.MAX_COMP_YEAR) {
                mCurrentSelectedYearPosition = 0;
            } else {
                mCurrentSelectedYearPosition = 1;
            }
            switchToModeForId(initNavId);
        }

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(true);
        // Only encourage learning on the launch of the app, not when the activity is
        // recreated from orientation changes
        encourageLearning(!fromSavedInstance);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensure that the correct navigation item is highlighted when returning to the StartActivity
        setNavigationDrawerItemSelected(mCurrentSelectedNavigationItemId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_YEAR_SPINNER_POSITION, mCurrentSelectedYearPosition);
        outState.putInt(STATE_SELECTED_NAV_ID, mCurrentSelectedNavigationItemId);
    }

    private void switchToModeForId(int id) {
        Fragment fragment;
        switch (id) {
            default:
            case R.id.nav_item_events:
                fragment = EventsByWeekFragment.newInstance(Constants.MAX_COMP_YEAR - mCurrentSelectedYearPosition);
                break;
            case R.id.nav_item_districts:
                fragment = DistrictListFragment.newInstance(Constants.MAX_COMP_YEAR - mCurrentSelectedYearPosition);
                break;
            case R.id.nav_item_teams:
                fragment = new AllTeamsListFragment();
                break;
            case R.id.nav_item_insights:
                fragment = new InsightsFragment();
                break;
            case R.id.nav_item_my_tba:
                fragment = new MyTBAFragment();
                break;
            case R.id.nav_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return;
        }
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in_support, R.anim.fade_out_support).replace(R.id.container, fragment, MAIN_FRAGMENT_TAG).commit();
        // This must be done before we lose the drawer
        mCurrentSelectedNavigationItemId = id;
    }

    private void resetActionBar() {
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            bar.setDisplayShowCustomEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // This will be triggered whenever the drawer opens or closes.
        if (!isDrawerOpen()) {
            resetActionBar();

            switch (mCurrentSelectedNavigationItemId) {
                case R.id.nav_item_events:
                    setupActionBarForEvents();
                    break;
                case R.id.nav_item_districts:
                    setupActionBarForDistricts();
                    break;
                case R.id.nav_item_teams:
                    getActionBar().setTitle("Teams");
                    break;
                case R.id.nav_item_insights:
                    getActionBar().setTitle("Insights");
                    break;
                case R.id.nav_item_my_tba:
                    getActionBar().setTitle("My TBA");
                    break;
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void setupActionBarForEvents() {
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);

            ArrayAdapter<String> actionBarAdapter = new ArrayAdapter<>(bar.getThemedContext(), R.layout.actionbar_spinner_events, R.id.year, eventsDropdownItems);
            actionBarAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            bar.setListNavigationCallbacks(actionBarAdapter, this);
            bar.setSelectedNavigationItem(mCurrentSelectedYearPosition);
        }

    }

    private void setupActionBarForDistricts() {
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);

            ArrayAdapter<String> actionBarAdapter = new ArrayAdapter<>(bar.getThemedContext(), R.layout.actionbar_spinner_districts, R.id.year, districtsDropdownItems);
            actionBarAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            bar.setListNavigationCallbacks(actionBarAdapter, this);
            if (mCurrentSelectedYearPosition >= 0 && mCurrentSelectedYearPosition < districtsDropdownItems.length) {
                bar.setSelectedNavigationItem(mCurrentSelectedYearPosition);
            } else {
                bar.setSelectedNavigationItem(0);
            }
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // Only handle this if the year has actually changed
        if (position == mCurrentSelectedYearPosition) {
            return true;
        }
        int selectedYear = Constants.MAX_COMP_YEAR - position;
        Log.d(Constants.LOG_TAG, "year selected: " + selectedYear);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in_support, R.anim.fade_out_support);
        if (mCurrentSelectedNavigationItemId == R.id.nav_item_events) {
            transaction = transaction.replace(R.id.container, EventsByWeekFragment.newInstance(selectedYear), MAIN_FRAGMENT_TAG);
        } else if (mCurrentSelectedNavigationItemId == R.id.nav_item_districts) {
            transaction = transaction.replace(R.id.container, DistrictListFragment.newInstance(selectedYear), MAIN_FRAGMENT_TAG);
        }
        transaction.commit();
        mCurrentSelectedYearPosition = position;
        getActionBar().setSelectedNavigationItem(mCurrentSelectedYearPosition);
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

    @Override
    public void showWarningMessage(String message) {
        warningMessage.setText(message);
        warningMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWarningMessage() {
        warningMessage.setVisibility(View.GONE);
    }
}
