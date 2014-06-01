package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.event.EventResultsFragment;

public class TeamAtEventActivity extends RefreshableHostActivity {

    public static final String EVENT = "eventKey", TEAM = "teamKey";

    private TextView warningMessage;
    private String eventKey, teamKey;

    public static Intent newInstance(Context c, String eventKey, String teamKey){
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
        if(extras != null && (extras.containsKey(EVENT) && extras.containsKey(TEAM))){
            teamKey = extras.getString(TEAM);
            eventKey = extras.getString(EVENT);
        }else{
            throw new IllegalArgumentException("TeamAtEventActivity must be constructed with event and team parameters");
        }

        getSupportFragmentManager().beginTransaction().add(R.id.content, EventResultsFragment.newInstance(eventKey, teamKey)).commit();
        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_view_event:
                startActivity(ViewEventActivity.newInstance(this, eventKey));
                break;
            case R.id.refresh:
                startRefresh();
                break;
            default:
                break;
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
