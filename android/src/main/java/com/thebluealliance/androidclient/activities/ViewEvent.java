package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.fragments.event.EventRankingsFragment;
import com.thebluealliance.androidclient.fragments.event.EventResultsFragment;
import com.thebluealliance.androidclient.fragments.event.EventStatsFragment;
import com.thebluealliance.androidclient.fragments.event.EventTeamsFragment;

/**
 * File created by phil on 4/20/14.
 */
public class ViewEvent extends Activity implements ActionBar.TabListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        //configure action bar tabs
        ActionBar bar = getActionBar();
        bar.setTitle("2014 Palmetto Regional"); //TEST DATA!
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        bar.addTab(bar.newTab().setText(getString(R.string.tab_event_info)).setTag("info").setTabListener(this));
        bar.addTab(bar.newTab().setText(getString(R.string.tab_event_teams)).setTag("teams").setTabListener(this));
        bar.addTab(bar.newTab().setText(getString(R.string.tab_event_results)).setTag("results").setTabListener(this));
        bar.addTab(bar.newTab().setText(getString(R.string.tab_event_rankings)).setTag("rankings").setTabListener(this));
        bar.addTab(bar.newTab().setText(getString(R.string.tab_event_stats)).setTag("stats").setTabListener(this));
        bar.addTab(bar.newTab().setText(getString(R.string.tab_event_awards)).setTag("awards").setTabListener(this));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Fragment fragment;
        switch(tab.getPosition()){
            default:case 0: //event info
                fragment = new EventInfoFragment();
                break;
            case 1: //teams
                fragment = new EventTeamsFragment();
                break;
            case 2: //results
                fragment = new EventResultsFragment();
                break;
            case 3: //rankings
                fragment = new EventRankingsFragment();
                break;
            case 4: //stats
                fragment = new EventStatsFragment();
                break;
            case 5: //awards
                fragment = new EventAwardsFragment();
                break;
        }
        getFragmentManager().beginTransaction().replace(R.id.event_container,fragment).commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
