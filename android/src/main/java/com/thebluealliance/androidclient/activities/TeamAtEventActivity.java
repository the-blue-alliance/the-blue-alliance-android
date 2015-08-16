package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewCompat;
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
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.TeamAtEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.modules.SubscriberModule;
import com.thebluealliance.androidclient.modules.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.modules.components.FragmentComponent;
import com.thebluealliance.androidclient.modules.components.HasFragmentComponent;
import com.thebluealliance.androidclient.views.SlidingTabs;

import java.util.Arrays;

public class TeamAtEventActivity extends FABNotificationSettingsActivity
  implements ViewPager.OnPageChangeListener, HasFragmentComponent {

    public static final String EVENT = "eventKey", TEAM = "teamKey";

    private TextView mWarningMessage;
    private String mEventKey, mTeamKey;
    private TeamAtEventFragmentPagerAdapter mAdapter;
    private FragmentComponent mComponent;

    public static Intent newInstance(Context c, String eventTeamKey) {
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

        // disable legacy RefreshableHostActivity
        setRefreshEnabled(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null && (extras.containsKey(EVENT) && extras.containsKey(TEAM))) {
            mTeamKey = extras.getString(TEAM);
            mEventKey = extras.getString(EVENT);
        } else {
            throw new IllegalArgumentException("TeamAtEventActivity must be constructed with event and team parameters");
        }

        String eventTeamKey = EventTeamHelper.generateKey(mEventKey, mTeamKey);
        setModelKey(eventTeamKey, ModelHelper.MODELS.EVENTTEAM);
        setContentView(R.layout.activity_team_at_event);

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new TeamAtEventFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey, mEventKey);
        pager.setAdapter(mAdapter);
        // To support refreshing, all pages must be held in memory at once
        // This should be increased if we ever add more pages
        pager.setOffscreenPageLimit(6);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setOnPageChangeListener(this);
        tabs.setViewPager(pager);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        mWarningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        setBeamUri(String.format(NfcUris.URI_TEAM_AT_EVENT, mEventKey, mTeamKey));

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
                startActivity(ViewEventActivity.newInstance(this, mEventKey));
                return true;
            case R.id.action_view_team:
                int year = Integer.parseInt(mEventKey.substring(0, 4));
                startActivity(ViewTeamActivity.newInstance(this, mTeamKey, year));
                return true;
            case R.id.stats_help:
                Utilities.showHelpDialog(this, R.raw.stats_help, getString(R.string.stats_help_title));
                return true;
            case android.R.id.home:
                if (isDrawerOpen()) {
                    closeDrawer();
                    return true;
                }
                Intent upIntent = ViewEventActivity.newInstance(this, mEventKey);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_teams))
                            .addNextIntent(ViewEventActivity.newInstance(this, mEventKey)).startActivities();
                } else {
                    Log.d(Constants.LOG_TAG, "Navigating up...");
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("");
    }

    @Override
    public void showWarningMessage(String message) {
        mWarningMessage.setText(message);
        mWarningMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWarningMessage() {
        mWarningMessage.setVisibility(View.GONE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mOptionsMenu != null) {
            if (position == Arrays.binarySearch(mAdapter.TITLES, "Stats")) {
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

    @SuppressWarnings(value = "unused")
    public void onEventMainThread(ActionBarTitleEvent event) {
        setActionBarTitle(event.getTitle());
        setActionBarSubtitle(event.getSubtitle());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public FragmentComponent getComponent() {
        if (mComponent == null) {
            TBAAndroid application = ((TBAAndroid) getApplication());
            mComponent = DaggerFragmentComponent.builder()
              .applicationComponent(application.getComponent())
              .datafeedModule(application.getDatafeedModule())
              .binderModule(application.getBinderModule())
              .databaseWriterModule(application.getDatabaseWriterModule())
              .subscriberModule(new SubscriberModule(this))
              .build();
        }
        return mComponent;
    }
}
