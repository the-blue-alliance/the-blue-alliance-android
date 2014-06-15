package com.thebluealliance.androidclient.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.listitems.EmptyListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.TeamListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;

/**
 * Created by Nathan on 6/15/2014.
 */
public class MoreSearchResultsActivity extends Activity {

    public static final int TEAM_RESULTS = 1;
    public static final int EVENT_RESULTS = 2;

    public static final String RESULTS_TYPE = "results_type";
    public static final String QUERY = "query";

    private ListView resultsList;

    public static Intent newInstance(Context c, int mode, String query) {
        Intent i = new Intent(c, MoreSearchResultsActivity.class);
        i.putExtra(RESULTS_TYPE, mode);
        i.putExtra(QUERY, query);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        resultsList = (ListView) findViewById(R.id.results);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("");

        String query = getIntent().getStringExtra(QUERY);
        int resultsType = getIntent().getIntExtra(RESULTS_TYPE, -1);
        if (query == null || resultsType == -1) {
            throw new IllegalArgumentException("MoreSearchResultsActivity most be created with a mode and query string!");
        }

        // Prepare text for query. We will split the query by spaces, append an asterisk to the end of
        // each component, and the put the string back together.
        String[] splitQuery = query.split("\\s+");

        for (int i = 0; i < splitQuery.length; i++) {
            splitQuery[i] = splitQuery[i] + "*";
        }

        String finalQuery = "";
        for (String aSplitQuery : splitQuery) {
            finalQuery += (aSplitQuery + " ");
        }

        ArrayList<ListItem> listItems = new ArrayList<>();
        switch (resultsType) {
            case TEAM_RESULTS:
                // Teams
                getActionBar().setTitle(String.format(getString(R.string.teams_matching), query));

                Cursor teamQueryResults = Database.getInstance(this).getMatchesForTeamQuery(finalQuery);
                if (teamQueryResults != null && teamQueryResults.moveToFirst()) {
                    teamQueryResults.moveToPosition(-1);
                    while (teamQueryResults.moveToNext()) {
                        String key = teamQueryResults.getString(teamQueryResults.getColumnIndex(Database.SearchTeam.KEY));
                        Team team = Database.getInstance(this).getTeam(key);
                        TeamListElement element = new TeamListElement(team);
                        listItems.add(element);
                    }
                } else {
                    listItems.add(new EmptyListElement(getString(R.string.no_teams_found)));
                }
                break;
            case EVENT_RESULTS:
                // Events
                getActionBar().setTitle(String.format(getString(R.string.events_matching), query));

                Cursor eventQueryResults = Database.getInstance(this).getMatchesForEventQuery(finalQuery);
                if (eventQueryResults != null && eventQueryResults.moveToFirst()) {
                    eventQueryResults.moveToPosition(-1);
                    while (eventQueryResults.moveToNext()) {
                        String key = eventQueryResults.getString(eventQueryResults.getColumnIndex(Database.SearchEvent.KEY));
                        Event event = Database.getInstance(this).getEvent(key);
                        EventListElement element = new EventListElement(event);
                        listItems.add(element);
                    }
                } else {
                    listItems.add(new EmptyListElement(getString(R.string.no_events_found)));
                }
                break;
        }

        ListViewAdapter adapter = new ListViewAdapter(this, listItems);
        resultsList.setAdapter(adapter);

        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ListViewAdapter adapter = (ListViewAdapter) adapterView.getAdapter();
                ListItem clickedItem = adapter.getItem(position);
                if (clickedItem instanceof TeamListElement) {
                    String teamKey = ((ListElement) clickedItem).getKey();
                    Intent i = new Intent(MoreSearchResultsActivity.this, ViewTeamActivity.class);
                    i.putExtra(ViewTeamActivity.TEAM_KEY, teamKey);
                    startActivity(i);
                } else if (clickedItem instanceof EventListElement) {
                    String eventKey = ((ListElement) clickedItem).getKey();
                    Intent intent = ViewEventActivity.newInstance(MoreSearchResultsActivity.this, eventKey);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
