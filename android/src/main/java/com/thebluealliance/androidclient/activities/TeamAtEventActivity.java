package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateTeamAtEvent;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Match;

public class TeamAtEventActivity extends RefreshableHostActivity implements RefreshListener {

    public static final String EVENT = "eventKey", TEAM = "teamKey";

    private TextView warningMessage;
    private String eventKey, teamKey;
    private PopulateTeamAtEvent task;

    public static Intent newInstance(Context c, String eventKey, String teamKey) {
        Intent intent = new Intent(c, TeamAtEventActivity.class);
        intent.putExtra(EVENT, eventKey);
        intent.putExtra(TEAM, teamKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_at_event);

        Bundle extras = getIntent().getExtras();
        if (extras != null && (extras.containsKey(EVENT) && extras.containsKey(TEAM))) {
            teamKey = extras.getString(TEAM);
            eventKey = extras.getString(EVENT);
        } else {
            throw new IllegalArgumentException("TeamAtEventActivity must be constructed with event and team parameters");
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);

        ((ExpandableListView) findViewById(R.id.results)).setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                if (view.findViewById(R.id.match_title) != null) {
                    String matchKey = view.findViewById(R.id.match_title).getTag().toString();
                    startActivity(ViewMatchActivity.newInstance(TeamAtEventActivity.this, matchKey));
                    return true;
                }

                return false;
            }
        });
        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        registerRefreshableActivityListener(this);

        setBeamUri(String.format(NfcUris.URI_TEAM_AT_EVENT, eventKey, teamKey));

    }

    @Override
    public void onResume() {
        super.onResume();
        startRefresh();
    }

    @Override
    public void onRefreshStart() {
        Log.i(Constants.REFRESH_LOG, teamKey+"@"+eventKey+" refresh started");
        task = new PopulateTeamAtEvent(this, true);
        task.execute(teamKey, eventKey);
    }

    @Override
    public void onRefreshStop() {
        task.cancel(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.team_at_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_event:
                startActivity(ViewEventActivity.newInstance(this, eventKey));
                break;
            case R.id.action_view_team:
                int year = Integer.parseInt(eventKey.substring(0,4));
                startActivity(ViewTeamActivity.newInstance(this, teamKey, year));
            case android.R.id.home:
                if(isDrawerOpen()) {
                    closeDrawer();
                    return true;
                }
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if(NavUtils.shouldUpRecreateTask(this, upIntent)) {
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
}
