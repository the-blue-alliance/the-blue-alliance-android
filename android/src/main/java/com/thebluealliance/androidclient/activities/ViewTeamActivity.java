package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentPagerAdapter;
import com.thebluealliance.androidclient.background.team.MakeActionBarDropdownForTeam;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.interfaces.OnYearChangedListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * File created by nathan on 4/21/14.
 */
public class ViewTeamActivity extends RefreshableHostActivity implements ActionBar.OnNavigationListener, ViewPager.OnPageChangeListener {

    public static final String TEAM_KEY = "team_key",
            TEAM_YEAR = "team_year",
            SELECTED_YEAR = "year",
            SELECTED_TAB = "tab";

    private TextView warningMessage;

    private int mCurrentSelectedYearPosition = -1,
            mSelectedTab = -1;

    private String[] yearsParticipated;

    // Should come in the format frc####
    private String mTeamKey;

    private int mYear;

    private ViewPager pager;

    // List of objects to notify when the year is changed
    private ArrayList<OnYearChangedListener> yearChangedListeners = new ArrayList<>();

    public static Intent newInstance(Context context, String teamKey) {
        System.out.println("making intent for " + teamKey);
        Intent intent = new Intent(context, ViewTeamActivity.class);
        intent.putExtra(TEAM_KEY, teamKey);
        return intent;
    }

    public static Intent newInstance(Context context, String teamKey, int year) {
        Intent intent = new Intent(context, ViewTeamActivity.class);
        intent.putExtra(TEAM_KEY, teamKey);
        intent.putExtra(TEAM_YEAR, year);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);

        mTeamKey = getIntent().getStringExtra(TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("ViewTeamActivity must be created with a team key!");
        }

        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_TAB)) {
                mSelectedTab = savedInstanceState.getInt(SELECTED_TAB);
            }
            if (savedInstanceState.containsKey(SELECTED_YEAR)) {
                mYear = savedInstanceState.getInt(SELECTED_YEAR);
            }
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(TEAM_YEAR)) {
                mYear = getIntent().getIntExtra(TEAM_YEAR, Calendar.getInstance().get(Calendar.YEAR));
            } else {
                mYear = Calendar.getInstance().get(Calendar.YEAR);
            }
            mCurrentSelectedYearPosition = 0;
            mSelectedTab = 0;
        }

        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setOffscreenPageLimit(3);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));
        // We will notify the fragments of the year later
        pager.setAdapter(new ViewTeamFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(this);

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        new MakeActionBarDropdownForTeam(this).execute(mTeamKey);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportFragmentManager().getFragments().clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_YEAR, getActionBar().getSelectedNavigationIndex());
        outState.putInt(SELECTED_TAB, mSelectedTab);
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
        encourageLearning(false);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isDrawerOpen()) {
            setupActionBar();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void setupActionBar() {
        if (yearsParticipated != null) {
            ActionBar bar = getActionBar();
            ArrayAdapter<String> actionBarAdapter = new ArrayAdapter<>(bar.getThemedContext(), R.layout.actionbar_spinner_team, R.id.year, yearsParticipated);
            actionBarAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);
            String teamNumber = mTeamKey.replace("frc", "");
            setActionBarTitle(String.format(getString(R.string.team_actionbar_title), teamNumber));
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            bar.setListNavigationCallbacks(actionBarAdapter, this);
            bar.setSelectedNavigationItem(mCurrentSelectedYearPosition);
        }
    }

    public void onYearsParticipatedLoaded(int[] years) {
        String[] dropdownItems = new String[years.length];
        int requestedYearIndex = 0;
        for (int i = 0; i < years.length; i++) {
            if (years[i] == mYear) {
                requestedYearIndex = i;
            }
            dropdownItems[i] = String.valueOf(years[i]);
        }
        yearsParticipated = dropdownItems;
        mCurrentSelectedYearPosition = requestedYearIndex;

        setupActionBar();

        notifyOnYearChangedListeners(Integer.parseInt(yearsParticipated[mCurrentSelectedYearPosition]));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isDrawerOpen()) {
                closeDrawer();
                return true;
            }

            // We recreate the back stack every time so we can assure that "up" goes to the teams view
            TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_teams)).startActivities();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public boolean onNavigationItemSelected(int position, long itemId) {
        if (position == mCurrentSelectedYearPosition) {
            return true;
        }
        Log.d(Constants.LOG_TAG, "year selected: " + Integer.parseInt(yearsParticipated[position]));

        mCurrentSelectedYearPosition = position;
        mYear = Integer.valueOf(yearsParticipated[mCurrentSelectedYearPosition]);

        notifyOnYearChangedListeners(mYear);

        setBeamUri(String.format(NfcUris.URI_TEAM_IN_YEAR, mTeamKey, mYear));

        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mSelectedTab = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void addOnYearChangedListener(OnYearChangedListener listener) {
        if (!yearChangedListeners.contains(listener)) {
            yearChangedListeners.add(listener);
        }
    }

    public void removeOnYearChangedListener(OnYearChangedListener listener) {
        if (yearChangedListeners.contains(listener)) {
            yearChangedListeners.remove(listener);
        }
    }

    private void notifyOnYearChangedListeners(int newYear) {
        Log.d(Constants.LOG_TAG, "notifying year changed");
        for (OnYearChangedListener listener : yearChangedListeners) {
            listener.onYearChanged(newYear);
        }
    }

    public int getCurrentSelectedYearPosition() {
        return mCurrentSelectedYearPosition;
    }
}
