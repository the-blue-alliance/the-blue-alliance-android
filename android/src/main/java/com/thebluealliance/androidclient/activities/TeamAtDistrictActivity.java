package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
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
import com.thebluealliance.androidclient.adapters.TeamAtDistrictFragmentPagerAdapter;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.views.SlidingTabs;

public class TeamAtDistrictActivity extends LegacyRefreshableHostActivity
  implements HasFragmentComponent {

    public static final String DISTRICT_KEY = "districtKey";
    public static final String TEAM_KEY = "teamKey";

    private String mDistrictKey;
    private String mTeamKey;
    private TextView mWarningMessage;
    private FragmentComponent mComponent;

    public static Intent newInstance(Context c, String teamAtDistrictKey) {
        return newInstance(c, DistrictTeamHelper.getTeamKey(teamAtDistrictKey), DistrictTeamHelper.getDistrictKey(teamAtDistrictKey));
    }

    public static Intent newInstance(Context c, String teamKey, String districtKey) {
        Intent intent = new Intent(c, TeamAtDistrictActivity.class);
        intent.putExtra(DISTRICT_KEY, districtKey);
        intent.putExtra(TEAM_KEY, teamKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_at_district);
        setRefreshEnabled(false);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(DISTRICT_KEY)) {
            mDistrictKey = getIntent().getExtras().getString(DISTRICT_KEY, "");
            if (!DistrictHelper.validateDistrictKey(mDistrictKey)) {
                throw new IllegalArgumentException("Invalid district key");
            }
        } else {
            throw new IllegalArgumentException("TeamAtDistrictActivity must be constructed with a district key");
        }
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TEAM_KEY)) {
            mTeamKey = getIntent().getExtras().getString(TEAM_KEY, "");
            if (!TeamHelper.validateTeamKey(mTeamKey)) {
                throw new IllegalArgumentException("Invalid team key");
            }
        } else {
            throw new IllegalArgumentException("TeamAtDistrictActivity must be constructed with a team key");
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWarningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        TeamAtDistrictFragmentPagerAdapter adapter = new TeamAtDistrictFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey, mDistrictKey);
        pager.setAdapter(adapter);
        // To support refreshing, all pages must be held in memory at once
        // This should be increased if we ever add more pages
        pager.setOffscreenPageLimit(10);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        setBeamUri(String.format(NfcUris.URI_TEAM_DISTRICT, mDistrictKey, mTeamKey));
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
        encourageLearning(false);
    }

    private void setupActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            setActionBarTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_at_district, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_view_district:
                startActivity(ViewDistrictActivity.newInstance(this, mDistrictKey));
                return true;
            case R.id.action_view_team:
                int year = Integer.parseInt(mDistrictKey.substring(0, 4));
                startActivity(ViewTeamActivity.newInstance(this, mTeamKey, year));
                return true;
            case android.R.id.home:
                if (isDrawerOpen()) {
                    closeDrawer();
                    return true;
                }
                Intent upIntent = ViewDistrictActivity.newInstance(this, mDistrictKey);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntent(upIntent).startActivities();
                } else {
                    Log.d(Constants.LOG_TAG, "Navigating up...");
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    @SuppressWarnings(value = "unused")
    public void onEventMainThread(ActionBarTitleEvent event) {
        setActionBarTitle(event.getTitle());
        setActionBarSubtitle(event.getSubtitle());
    }

    @Override
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
