package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentAdapter;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.fragments.TeamInfoFragment;

/**
 * File created by nathan on 4/21/14.
 */
public class ViewTeam extends Activity {

    public static final String TEAM_KEY = "team_key";

    private static final String INFO_TAB_TAG = "info";
    private static final String EVENTS_TAB_TAG = "events";
    private static final String MEDIA_TAB_TAG = "media";
    private String mCurrentTabTag = "";

    // Should come in the format frc####
    private String mTeamKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);

        mTeamKey = getIntent().getStringExtra(TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("ViewTeam must be created with a team key!");
        }

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new ViewTeamFragmentAdapter(getFragmentManager(), mTeamKey));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        // Setup the action bar
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar bar = getActionBar();
        if (bar != null) {
            // Setup the title
            String teamNumber = mTeamKey.replace("frc", "");
            getActionBar().setTitle("Team " + teamNumber);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
