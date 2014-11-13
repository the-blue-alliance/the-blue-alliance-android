package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.TeamAtEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.views.SlidingTabs;

import java.util.Arrays;

public class TeamAtEventActivity extends FABNotificationSettingsActivity implements ViewPager.OnPageChangeListener {

    public static final String EVENT = "eventKey", TEAM = "teamKey";

    private TextView warningMessage;
    private String eventKey, teamKey;
    private ViewPager pager;
    private TeamAtEventFragmentPagerAdapter adapter;

    public static Intent newInstance(Context c, String eventTeamKey){
        return newInstance(c, EventTeamHelper.getEventKey(eventTeamKey), EventTeamHelper.getTeamKey(eventTeamKey));
    }

    public static Intent newInstance(Context c, String eventKey, String teamKey) {
        Intent intent = new Intent(c, TeamAtEventActivity.class);
        intent.putExtra(EVENT, eventKey);
        intent.putExtra(TEAM, teamKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null && (extras.containsKey(EVENT) && extras.containsKey(TEAM))) {
            teamKey = extras.getString(TEAM);
            eventKey = extras.getString(EVENT);
        } else {
            throw new IllegalArgumentException("TeamAtEventActivity must be constructed with event and team parameters");
        }

        String eventTeamKey = EventTeamHelper.generateKey(eventKey, teamKey);
        setModelKey(eventTeamKey);
        setContentView(R.layout.activity_team_at_event);

        pager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new TeamAtEventFragmentPagerAdapter(getSupportFragmentManager(), teamKey, eventKey);
        pager.setAdapter(adapter);
        // To support refreshing, all pages must be held in memory at once
        // This should be increased if we ever add more pages
        pager.setOffscreenPageLimit(6);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setOnPageChangeListener(this);
        tabs.setViewPager(pager);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        setBeamUri(String.format(NfcUris.URI_TEAM_AT_EVENT, eventKey, teamKey));

        startRefresh();

        setSettingsToolbarTitle("Team at Event Settings");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.team_at_event, menu);
        getMenuInflater().inflate(R.menu.stats_help_menu, menu);
        mOptionsMenu = menu;
        mOptionsMenu.findItem(R.id.stats_help).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_event:
                startActivity(ViewEventActivity.newInstance(this, eventKey));
                return true;
            case R.id.action_view_team:
                int year = Integer.parseInt(eventKey.substring(0, 4));
                startActivity(ViewTeamActivity.newInstance(this, teamKey, year));
                return true;
            case R.id.stats_help:
                Utilities.showHelpDialog(this, R.raw.stats_help, getString(R.string.stats_help_title));
                return true;
            case android.R.id.home:
                if (isDrawerOpen()) {
                    closeDrawer();
                    return true;
                }
                Intent upIntent = ViewEventActivity.newInstance(this, eventKey);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_teams))
                            .addNextIntent(ViewEventActivity.newInstance(this, eventKey)).startActivities();
                } else {
                    Log.d(Constants.LOG_TAG, "Navigating up...");
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mOptionsMenu != null) {
            if (position == Arrays.binarySearch(adapter.TITLES, "Stats")) {
                //stats position
                mOptionsMenu.findItem(R.id.stats_help).setVisible(true);
            } else {
                mOptionsMenu.findItem(R.id.stats_help).setVisible(false);
            }
        }

        // hide the FAB if we aren't on the first page
        if (position != 0) {
            hideFab(true);
        } else {
            showFab(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
