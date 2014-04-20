package com.thebluealliance.androidtest.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thebluealliance.androidtest.R;
import com.thebluealliance.androidtest.fragments.EventListFragment;
import com.thebluealliance.androidtest.fragments.InsightsFragment;
import com.thebluealliance.androidtest.fragments.TeamListFragment;


public class TBA_Start extends Activity implements ActionBar.TabListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item",
                                EVENT_TAG = "events",
                                TEAM_TAG  = "teams",
                                INSIGHTS_TAG = "insights";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.title_main_activity);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //set up action bar tabs for main navigation
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.tab_events)).setTag(EVENT_TAG).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.tab_teams)).setTag(TEAM_TAG).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.tab_insights)).setTag(INSIGHTS_TAG).setTabListener(this));

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_list, menu);
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
        // When the given dropdown item is selected, show its contents in the container view.
        // Tabs are (in order) - events, teams, insights
        Fragment content; //Fragment to inflate into the content view
        switch(tab.getTag().toString()){
            default: case EVENT_TAG: //events
                content = new EventListFragment();
                break;
            case TEAM_TAG: //teams
                content = new TeamListFragment();
                break;
            case INSIGHTS_TAG: //insights
                content = new InsightsFragment();
                break;
        }
        getFragmentManager().beginTransaction().replace(R.id.container, content).commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
