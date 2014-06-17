package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;

import java.util.Calendar;

/**
 * File created by nathan on 4/21/14.
 */
public class ViewTeamActivity extends RefreshableHostActivity implements ActionBar.OnNavigationListener, ViewPager.OnPageChangeListener {

    public static final String TEAM_KEY = "team_key",
            SELECTED_YEAR = "year",
            SELECTED_TAB = "tab";

    private TextView warningMessage;

    private int mCurrentSelectedYearPosition = -1,
            mSelectedTab = -1;

    private String[] dropdownItems;

    // Should come in the format frc####
    private String mTeamKey;

    private ViewPager pager;

    public static Intent newInstance(Context context, String teamKey) {
        System.out.println("making intent for " + teamKey);
        Intent intent = new Intent(context, ViewTeamActivity.class);
        intent.putExtra(TEAM_KEY, teamKey);
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

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        dropdownItems = new String[currentYear - Constants.FIRST_COMP_YEAR + 1];
        for (int i = 0; i < dropdownItems.length; i++) {
            dropdownItems[i] = Integer.toString(currentYear - i);
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_TAB)) {
                mSelectedTab = savedInstanceState.getInt(SELECTED_TAB);
            }
            if (savedInstanceState.containsKey(SELECTED_YEAR)) {
                mCurrentSelectedYearPosition = savedInstanceState.getInt(SELECTED_YEAR);
            }
        } else {
            mCurrentSelectedYearPosition = 0;
            mSelectedTab = 0;
        }

        pager = (ViewPager) findViewById(R.id.view_pager);
        int year = Integer.parseInt(dropdownItems[mCurrentSelectedYearPosition]);
        pager.setAdapter(new ViewTeamFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey, year));
        pager.setOnPageChangeListener(this);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(this);

        // Setup the action bar
        resetActionBar();
        if (mSelectedTab == 0) {
            setupActionBar();
        } else {
            setupActionBarForYear();
        }

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        setBeamUri(String.format(NfcUris.URI_TEAM, mTeamKey));
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportFragmentManager().getFragments().clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_YEAR, mCurrentSelectedYearPosition);
        outState.putInt(SELECTED_TAB, mSelectedTab);
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
        encourageLearning(false);
    }

    private void resetActionBar() {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
    }

    private void setupActionBar() {
        String teamNumber = mTeamKey.replace("frc", "");
        setActionBarTitle(String.format(getString(R.string.team_actionbar_title), teamNumber));
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupActionBarForYear() {
        ArrayAdapter<String> actionBarAdapter = new ArrayAdapter<>(getActionBar().getThemedContext(), R.layout.actionbar_spinner_team, R.id.year, dropdownItems);
        actionBarAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);
        String teamNumber = mTeamKey.replace("frc", "");
        ActionBar bar = getActionBar();
        setActionBarTitle(String.format(getString(R.string.team_actionbar_title), teamNumber) + " - ");
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        bar.setListNavigationCallbacks(actionBarAdapter, this);
        bar.setSelectedNavigationItem(mCurrentSelectedYearPosition);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isDrawerOpen()) {
            setupActionBar();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
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
        Log.d(Constants.LOG_TAG, "year selected: " + Integer.parseInt(dropdownItems[position]));

        getSupportFragmentManager().getFragments().clear();
        int year = Integer.parseInt(dropdownItems[position]);
        pager.setAdapter(new ViewTeamFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey, year));
        pager.setCurrentItem(mSelectedTab);
        mCurrentSelectedYearPosition = position;

        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == mSelectedTab)
            return;

        switch (position) {
            case 0:
                resetActionBar();
                setupActionBar();
                break;
            case 1:
            case 2:
                resetActionBar();
                setupActionBarForYear();
                break;
        }
        mSelectedTab = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
