package com.thebluealliance.androidclient.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
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

public class HomeActivity extends RefreshableHostActivity {

    /**
     * Saved instance state key representing the last select navigation drawer item
     */
    private static final String STATE_SELECTED_NAV_ID = "selected_navigation_drawer_position";

    private static final String REQUESTED_MODE = "requested_mode";

    /**
     * The serialization (saved instance state) Bundle key representing the current dropdown
     * position.
     */
    private static final String STATE_SELECTED_YEAR_SPINNER_POSITION = "selected_spinner_position";

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";

    private boolean fromSavedInstance = false;

    private int mCurrentSelectedNavigationItemId = -1;
    private int mCurrentSelectedYearPosition = -1;

    private String[] eventsDropdownItems, districtsDropdownItems;

    private TextView warningMessage;

    private Toolbar toolbar;
    private View yearSelectorContainer;
    private TextView yearSelectorTitle;

    public static Intent newInstance(Context context, int requestedMode) {
        Intent i = new Intent(context, HomeActivity.class);
        i.putExtra(REQUESTED_MODE, requestedMode);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        yearSelectorContainer = findViewById(R.id.year_selector_container);
        yearSelectorTitle = (TextView) findViewById(R.id.year_selector_title);

        warningMessage = (TextView) findViewById(R.id.warning_container);

        hideWarningMessage();

        handler = new Handler();

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

            if (savedInstanceState.containsKey(STATE_SELECTED_YEAR_SPINNER_POSITION)) {
                mCurrentSelectedYearPosition = savedInstanceState.getInt(STATE_SELECTED_YEAR_SPINNER_POSITION);
            } else {
                if (Calendar.getInstance().get(Calendar.YEAR) == Constants.MAX_COMP_YEAR) {
                    mCurrentSelectedYearPosition = 0;
                } else {
                    mCurrentSelectedYearPosition = 1;
                }
            }

            if (savedInstanceState.containsKey(STATE_SELECTED_NAV_ID)) {
                mCurrentSelectedNavigationItemId = savedInstanceState.getInt(STATE_SELECTED_NAV_ID);
                switchToModeForId(mCurrentSelectedNavigationItemId);
            } else {
                switchToModeForId(R.id.nav_item_events);
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
            case R.id.nav_item_notifications:
                startActivity(NotificationDashboardActivity.newInstance(this));
                return;
            case R.id.nav_item_gameday:
                startActivity(GamedayActivity.newInstance(this));
                return;
        }
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in_support, R.anim.fade_out_support).replace(R.id.container, fragment, MAIN_FRAGMENT_TAG).commit();
        // This must be done before we lose the drawer
        mCurrentSelectedNavigationItemId = id;

        // Call this to make sure the toolbar has the correct contents
        invalidateOptionsMenu();

        // The Districts fragment doesn't have tabs to set an elevation to, so we have to apply an elevation to the toolbar here
        if (mCurrentSelectedNavigationItemId == R.id.nav_item_districts) {
            ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        } else {
            ViewCompat.setElevation(toolbar, 0);
        }
    }

    private void resetActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            yearSelectorContainer.setVisibility(View.GONE);
            //bar.setDisplayShowCustomEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // This will be triggered whenever the drawer opens or closes.
        resetActionBar();

        switch (mCurrentSelectedNavigationItemId) {
            case R.id.nav_item_events:
                setupActionBarForEvents();
                break;
            case R.id.nav_item_districts:
                setupActionBarForDistricts();
                break;
            case R.id.nav_item_teams:
                getSupportActionBar().setTitle("Teams");
                toolbar.setContentInsetsAbsolute(Utilities.getPixelsFromDp(this, 72), 0);
                break;
            case R.id.nav_item_insights:
                getSupportActionBar().setTitle("Insights");
                toolbar.setContentInsetsAbsolute(Utilities.getPixelsFromDp(this, 72), 0);
                break;
            case R.id.nav_item_my_tba:
                getSupportActionBar().setTitle("myTBA");
                toolbar.setContentInsetsAbsolute(Utilities.getPixelsFromDp(this, 72), 0);
                break;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void setupActionBarForEvents() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(false);

        yearSelectorContainer.setVisibility(View.VISIBLE);

        final Dialog dialog = makeDialogForYearSelection(R.string.select_year, eventsDropdownItems);

        yearSelectorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        if (mCurrentSelectedYearPosition >= 0 && mCurrentSelectedYearPosition < eventsDropdownItems.length) {
            onYearSelected(mCurrentSelectedYearPosition);
            updateEventsYearSelector(mCurrentSelectedYearPosition);
        } else {
            onYearSelected(0);
            updateEventsYearSelector(0);
        }
    }

    private void updateEventsYearSelector(int selectedPosition) {
        Resources res = getResources();
        yearSelectorTitle.setText(String.format(res.getString(R.string.year_selector_title_events), eventsDropdownItems[selectedPosition]));
    }

    private void setupActionBarForDistricts() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(false);

        yearSelectorContainer.setVisibility(View.VISIBLE);

        final Dialog dialog = makeDialogForYearSelection(R.string.select_year, districtsDropdownItems);

        yearSelectorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        if (mCurrentSelectedYearPosition >= 0 && mCurrentSelectedYearPosition < eventsDropdownItems.length) {
            onYearSelected(mCurrentSelectedYearPosition);
            updateDistrictsYearSelector(mCurrentSelectedYearPosition);
        } else {
            onYearSelected(0);
            updateDistrictsYearSelector(0);
        }
    }

    private void updateDistrictsYearSelector(int selectedPosition) {
        Resources res = getResources();
        yearSelectorTitle.setText(String.format(res.getString(R.string.year_selector_title_districts), districtsDropdownItems[selectedPosition]));
    }

    private Dialog makeDialogForYearSelection(@StringRes int titleResId, String[] dropdownItems) {
        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(res.getString(titleResId));
        builder.setItems(dropdownItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onYearSelected(which);
            }
        });

        return builder.create();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        // Don't reload the fragment if the user selects the tab we are currently on
        final int id = item.getId();
        if (id != mCurrentSelectedNavigationItemId) {
            // Launch after a short delay to give the drawer time to close.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchToModeForId(id);
                }
            }, DRAWER_CLOSE_ANIMATION_DURATION);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(Constants.LOG_TAG, "New intent received!");
        if (intent != null && intent.getExtras() != null) {
            int requestedMode = intent.getExtras().getInt(REQUESTED_MODE, R.id.nav_item_events);
            if (requestedMode == mCurrentSelectedNavigationItemId) {
                // We are already in the appropriate mode
                Log.d(Constants.LOG_TAG, "Same requested mode");
                return;
            } else {
                Log.d(Constants.LOG_TAG, "New requested mode");
                switchToModeForId(requestedMode);
                // Ensure that the Action Bar is properly configured for the current mode
                invalidateOptionsMenu();
            }
        } else {
            /* No intent given. Switch to default mode */
            switchToModeForId(mCurrentSelectedNavigationItemId);
            invalidateOptionsMenu();
        }
    }

    public void onEvent() {

    }


    private void onYearSelected(int position) {
        // Only handle this if the year has actually changed
        if (position == mCurrentSelectedYearPosition) {
            return;
        }
        int selectedYear = Constants.MAX_COMP_YEAR - position;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in_support, R.anim.fade_out_support);
        if (mCurrentSelectedNavigationItemId == R.id.nav_item_events) {
            transaction = transaction.replace(R.id.container, EventsByWeekFragment.newInstance(selectedYear), MAIN_FRAGMENT_TAG);
            updateEventsYearSelector(position);
        } else if (mCurrentSelectedNavigationItemId == R.id.nav_item_districts) {
            transaction = transaction.replace(R.id.container, DistrictListFragment.newInstance(selectedYear), MAIN_FRAGMENT_TAG);
            updateDistrictsYearSelector(position);
        }
        transaction.commit();
        mCurrentSelectedYearPosition = position;
    }
}
