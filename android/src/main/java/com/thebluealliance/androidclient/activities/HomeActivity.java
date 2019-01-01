package com.thebluealliance.androidclient.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.settings.SettingsActivity;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.fragments.AllTeamsListFragment;
import com.thebluealliance.androidclient.fragments.EventsByWeekFragment;
import com.thebluealliance.androidclient.fragments.GamedayFragment;
import com.thebluealliance.androidclient.fragments.RecentNotificationsFragment;
import com.thebluealliance.androidclient.fragments.district.DistrictListFragment;
import com.thebluealliance.androidclient.fragments.mytba.MyTBAFragment;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.listitems.NavDrawerItem;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;

import java.util.Arrays;

import javax.inject.Inject;

public class HomeActivity extends DatafeedActivity implements HasFragmentComponent {

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

    @Inject TBAStatusController mStatusController;

    private boolean mFromSavedInstance = false;
    private int mCurrentSelectedNavigationItemId = -1;
    private int mCurrentSelectedYearPosition = -1;
    private int mSelectedYear;
    private String[] mEventsDropdownItems, mDistrictsDropdownItems;
    private Toolbar mToolbar;
    private View mYearSelectorContainer;
    private TextView mYarSelectorTitle;
    private FragmentComponent mComponent;
    private int mMaxCompYear;

    public static Intent newInstance(Context context, int requestedMode) {
        Intent i = new Intent(context, HomeActivity.class);
        i.putExtra(REQUESTED_MODE, requestedMode);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        inject();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mYearSelectorContainer = findViewById(R.id.year_selector_container);
        mYarSelectorTitle = (TextView) findViewById(R.id.year_selector_title);

        handler = new Handler();
        mMaxCompYear = mStatusController.getMaxCompYear();

        mEventsDropdownItems = new String[mMaxCompYear - Constants.FIRST_COMP_YEAR + 1];
        for (int i = 0; i < mEventsDropdownItems.length; i++) {
            mEventsDropdownItems[i] = Integer.toString(mMaxCompYear - i);
        }

        mDistrictsDropdownItems = new String[mMaxCompYear - Constants.FIRST_DISTRICT_YEAR + 1];
        for (int i = 0; i < mDistrictsDropdownItems.length; i++) {
            mDistrictsDropdownItems[i] = Integer.toString(mMaxCompYear - i);
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

        String currentYear = Integer.toString(mStatusController.getCurrentCompYear());
        if (savedInstanceState != null) {
            mFromSavedInstance = true;
            TbaLogger.d("StartActivity is from saved instance");

            if (savedInstanceState.containsKey(STATE_SELECTED_YEAR_SPINNER_POSITION)) {
                mCurrentSelectedYearPosition = savedInstanceState.getInt(STATE_SELECTED_YEAR_SPINNER_POSITION);
            } else {
                mCurrentSelectedYearPosition = Arrays.asList(mEventsDropdownItems)
                                                     .indexOf(currentYear);
                if (mCurrentSelectedNavigationItemId == -1) {
                    mCurrentSelectedNavigationItemId = 0;
                }
            }

            if (savedInstanceState.containsKey(STATE_SELECTED_NAV_ID)) {
                mCurrentSelectedNavigationItemId = savedInstanceState.getInt(STATE_SELECTED_NAV_ID);
                switchToModeForId(mCurrentSelectedNavigationItemId, savedInstanceState);
            } else {
                switchToModeForId(R.id.nav_item_events, savedInstanceState);
            }
        } else {
            mCurrentSelectedYearPosition = Arrays.asList(mEventsDropdownItems)
                                                 .indexOf(currentYear);
            if (mCurrentSelectedNavigationItemId == -1) {
                    mCurrentSelectedNavigationItemId = 0;
            }
            switchToModeForId(initNavId, savedInstanceState);
        }


        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(BaseActivity.WARNING_OFFLINE);
        }
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(true);
        // Only encourage learning on the launch of the app, not when the activity is
        // recreated from orientation changes
        encourageLearning(!mFromSavedInstance);
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
        Fragment subFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (subFragment != null) {
            subFragment.onSaveInstanceState(outState);
        }
    }

    private void switchToModeForId(int id, Bundle savedInstanceState) {
        Fragment fragment;
        int year = mMaxCompYear - mCurrentSelectedYearPosition;
        switch (id) {
            default:
            case R.id.nav_item_events:
                int weekTab = savedInstanceState != null
                        ? savedInstanceState.getInt(EventsByWeekFragment.TAB, -1)
                        : -1;
                fragment = EventsByWeekFragment.newInstance(year, weekTab);
                break;
            case R.id.nav_item_districts:
                fragment = DistrictListFragment.newInstance(year);
                break;
            case R.id.nav_item_teams:
                int teamTab = savedInstanceState != null
                        ? savedInstanceState.getInt(AllTeamsListFragment.SELECTED_TAB, 0)
                        : 0;
                fragment = AllTeamsListFragment.newInstance(teamTab);
                break;
            case R.id.nav_item_my_tba:
                fragment = new MyTBAFragment();
                break;
            case R.id.nav_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return;
            case R.id.nav_item_notifications:
                fragment = new RecentNotificationsFragment();
                break;
            case R.id.nav_item_gameday:
                int gamedayTab = savedInstanceState != null
                        ? savedInstanceState.getInt(GamedayFragment.SELECTED_TAB, 0)
                        : 0;
                fragment = GamedayFragment.newInstance(gamedayTab);
                break;
        }
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in_support, R.anim.fade_out_support).replace(R.id.container, fragment, MAIN_FRAGMENT_TAG).commit();
        // This must be done before we lose the drawer
        mCurrentSelectedNavigationItemId = id;

        // Call this to make sure the toolbar has the correct contents
        setupToolbarForCurrentMode();

        // The Districts & Notifications fragments don't have tabs to set an elevation to, so we
        // have to apply an elevation to the toolbar here
        if (mCurrentSelectedNavigationItemId == R.id.nav_item_districts
                || mCurrentSelectedNavigationItemId == R.id.nav_item_notifications) {
            ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        } else {
            ViewCompat.setElevation(mToolbar, 0);
        }
    }

    private void resetActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            mYearSelectorContainer.setVisibility(View.GONE);
            bar.setDisplayShowTitleEnabled(true);
        }
    }

    private void setupToolbarForCurrentMode() {
        resetActionBar();
        setSearchEnabled(true);
        setRefreshEnabled(true);

        switch (mCurrentSelectedNavigationItemId) {
            case R.id.nav_item_events:
                setupActionBarForEvents();
                mToolbar.setContentInsetsAbsolute(0, 0);
                break;
            case R.id.nav_item_districts:
                setupActionBarForDistricts();
                mToolbar.setContentInsetsAbsolute(0, 0);
                break;
            case R.id.nav_item_teams:
                getSupportActionBar().setTitle(R.string.teams);
                mToolbar.setContentInsetsAbsolute(Utilities.getPixelsFromDp(this, 72), 0);
                break;
            case R.id.nav_item_my_tba:
                getSupportActionBar().setTitle(R.string.mytba);
                mToolbar.setContentInsetsAbsolute(Utilities.getPixelsFromDp(this, 72), 0);
                break;
            case R.id.nav_item_notifications:
                getSupportActionBar().setTitle(R.string.notifications);
                setRefreshEnabled(false);
                mToolbar.setContentInsetsAbsolute(Utilities.getPixelsFromDp(this, 72), 0);
                break;
            case R.id.nav_item_gameday:
                getSupportActionBar().setTitle(R.string.title_activity_gameday);
                mToolbar.setContentInsetsAbsolute(Utilities.getPixelsFromDp(this, 72), 0);
                break;
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (mCurrentSelectedNavigationItemId) {
            case R.id.nav_item_notifications:
                getMenuInflater().inflate(R.menu.recent_notifications_help_menu, menu);
                break;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recent_notifications_help:
                Utilities.showHelpDialog(this, R.raw.recent_notifications_help, getString(R.string.action_help));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBarForEvents() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mYearSelectorContainer.setVisibility(View.VISIBLE);

        final Dialog dialog = makeDialogForYearSelection(R.string.select_year, mEventsDropdownItems);

        mYearSelectorContainer.setOnClickListener(v -> dialog.show());

        if (mCurrentSelectedYearPosition >= 0 && mCurrentSelectedYearPosition < mEventsDropdownItems.length) {
            onYearSelected(mCurrentSelectedYearPosition);
            updateEventsYearSelector(mCurrentSelectedYearPosition);
        } else {
            onYearSelected(0);
            updateEventsYearSelector(0);
        }
    }

    private void updateEventsYearSelector(int selectedPosition) {
        Resources res = getResources();
        mYarSelectorTitle.setText(String.format(res.getString(R.string.year_selector_title_events), mEventsDropdownItems[selectedPosition]));
    }

    private void setupActionBarForDistricts() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mYearSelectorContainer.setVisibility(View.VISIBLE);

        final Dialog dialog = makeDialogForYearSelection(R.string.select_year, mDistrictsDropdownItems);

        mYearSelectorContainer.setOnClickListener(v -> dialog.show());
        if (mCurrentSelectedYearPosition >= 0
                && mCurrentSelectedYearPosition < mEventsDropdownItems.length
                && mCurrentSelectedYearPosition < mDistrictsDropdownItems.length) {
            onYearSelected(mCurrentSelectedYearPosition);
            updateDistrictsYearSelector(mCurrentSelectedYearPosition);
        } else {
            onYearSelected(0);
            updateDistrictsYearSelector(0);
        }
    }

    private void updateDistrictsYearSelector(int selectedPosition) {
        Resources res = getResources();
        mYarSelectorTitle.setText(String.format(res.getString(R.string.year_selector_title_districts), mDistrictsDropdownItems[selectedPosition]));
    }

    private Dialog makeDialogForYearSelection(@StringRes int titleResId, String[] dropdownItems) {
        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(res.getString(titleResId));
        builder.setItems(dropdownItems, (dialog, which) -> {
            onYearSelected(which);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

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
            handler.postDelayed(() -> switchToModeForId(id, null), DRAWER_CLOSE_ANIMATION_DURATION);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.getExtras() != null) {
            int requestedMode = intent.getExtras().getInt(REQUESTED_MODE, R.id.nav_item_events);
            if (requestedMode == mCurrentSelectedNavigationItemId) {
                // We are already in the appropriate mode
                return;
            } else {
                switchToModeForId(requestedMode, null);
            }
        } else {
            // No intent given. Switch to default mode
            switchToModeForId(mCurrentSelectedNavigationItemId, null);
        }
    }

    private void onYearSelected(int position) {
        // Only handle this if the year has actually changed
        if (position == mCurrentSelectedYearPosition) {
            return;
        }
        int selectedYear = mMaxCompYear - position;
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

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    public FragmentComponent getComponent() {
        if (mComponent == null) {
            TBAAndroid application = ((TBAAndroid) getApplication());
            mComponent = DaggerFragmentComponent.builder()
                    .applicationComponent(application.getComponent())
                    .datafeedModule(application.getDatafeedModule())
                    .binderModule(application.getBinderModule())
                    .databaseWriterModule(application.getDatabaseWriterModule())
                    .gceModule(application.getGceModule())
                    .authModule(application.getAuthModule())
                    .subscriberModule(new SubscriberModule(this))
                    .clickListenerModule(new ClickListenerModule(this))
                    .build();
        }
        return mComponent;
    }
}
