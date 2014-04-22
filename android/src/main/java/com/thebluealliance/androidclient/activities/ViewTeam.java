package com.thebluealliance.androidclient.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.PopulateEventMatches;
import com.thebluealliance.androidclient.fragments.TeamInfoFragment;

/**
 * File created by nathan on 4/21/14.
 */
public class ViewTeam extends Activity {

    public static final String TEAM_KEY = "team_key";

    // Should come in the format frc####
    private String mTeamKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);

        mTeamKey = getIntent().getStringExtra(TEAM_KEY);
        if(mTeamKey == null) {
            throw new IllegalArgumentException("ViewTeam must be created with a team key!");
        }

        Fragment f = new TeamInfoFragment();
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, mTeamKey);
        f.setArguments(args);
        getFragmentManager().beginTransaction().add(R.id.content, f).commit();

        // Setup the action bar
        String teamNumber = mTeamKey.replace("frc", "");
        getActionBar().setTitle("Team " + teamNumber);
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

}
