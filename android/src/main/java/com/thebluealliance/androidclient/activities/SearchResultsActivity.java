package com.thebluealliance.androidclient.activities;

import android.app.Activity;
import android.app.SearchManager;
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
import com.thebluealliance.androidclient.datatypes.EmptyListElement;
import com.thebluealliance.androidclient.datatypes.EventListElement;
import com.thebluealliance.androidclient.datatypes.EventWeekHeader;
import com.thebluealliance.androidclient.datatypes.ListElement;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.TeamListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;

/**
 * Created by Nathan on 6/14/2014.
 */
public class SearchResultsActivity extends Activity {

    private static final int MAX_RESULTS_PER_CATEGORY = 5;

    ListView resultsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        resultsList = (ListView) findViewById(R.id.results);
        handleIntent(getIntent());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        // Set empty title for now; we will replace it with an appropriate title when we
        // handle the intent
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            getActionBar().setTitle(String.format(getString(R.string.search_action_bar_title), query));

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

            // Teams
            Cursor teamQueryResults = Database.getInstance(this).getMatchesForTeamQuery(finalQuery);
            listItems.add(new EventWeekHeader(getString(R.string.teams_header)));
            if (teamQueryResults != null && teamQueryResults.moveToFirst()) {
                teamQueryResults.moveToPosition(-1);
                while (teamQueryResults.moveToNext()) {
                    // Limit ourselves to a certain number of teams
                    if (teamQueryResults.getPosition() >= MAX_RESULTS_PER_CATEGORY) {
                        break;
                    }
                    String key = teamQueryResults.getString(teamQueryResults.getColumnIndex(Database.SearchTeam.KEY));
                    Team team = Database.getInstance(this).getTeam(key);
                    TeamListElement element = new TeamListElement(team);
                    listItems.add(element);
                }
            } else {
                listItems.add(new EmptyListElement(getString(R.string.no_teams_found)));
            }

            // Events
            Cursor eventQueryResults = Database.getInstance(this).getMatchesForEventQuery(finalQuery);
            listItems.add(new EventWeekHeader(getString(R.string.events_header)));
            if (eventQueryResults != null && eventQueryResults.moveToFirst()) {
                eventQueryResults.moveToPosition(-1);

                while (eventQueryResults.moveToNext()) {
                    // Limit ourselves to a certain number of events
                    if (eventQueryResults.getPosition() >= MAX_RESULTS_PER_CATEGORY) {
                        break;
                    }
                    String key = eventQueryResults.getString(eventQueryResults.getColumnIndex(Database.SearchEvent.KEY));
                    Event event = Database.getInstance(this).getEvent(key);
                    EventListElement element = new EventListElement(event);
                    listItems.add(element);
                }
            } else {
                listItems.add(new EmptyListElement(getString(R.string.no_events_found)));
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
                        Intent i = new Intent(SearchResultsActivity.this, ViewTeamActivity.class);
                        i.putExtra(ViewTeamActivity.TEAM_KEY, teamKey);
                        startActivity(i);
                    } else if (clickedItem instanceof EventListElement) {
                        String eventKey = ((ListElement) clickedItem).getKey();
                        Intent intent = ViewEventActivity.newInstance(SearchResultsActivity.this, eventKey);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}
