package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.listitems.EmptyListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.SearchResultsHeaderListElement;
import com.thebluealliance.androidclient.listitems.TeamListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;

/**
 * Created by Nathan on 6/14/2014.
 */
public class SearchResultsActivity extends NavigationDrawerActivity implements SearchView.OnQueryTextListener {

    private static final int MAX_RESULTS_PER_CATEGORY = 5;

    ListView resultsList;

    private SearchResultsHeaderListElement teamsHeader, eventsHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        resultsList = (ListView) findViewById(R.id.results);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        SearchView searchView = new SearchView(getActionBar().getThemedContext());
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(searchView, layoutParams);
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

    private void updateQuery(final String query) {

        String finalQuery = Utilities.getPreparedQueryForSearch(query);

        ArrayList<ListItem> listItems = new ArrayList<>();

        // Teams
        Cursor teamQueryResults = Database.getInstance(this).getMatchesForTeamQuery(finalQuery);
        if (teamQueryResults != null && teamQueryResults.moveToFirst()) {
            teamQueryResults.moveToPosition(-1);

            teamsHeader = new SearchResultsHeaderListElement(getString(R.string.teams_header));
            if (teamQueryResults.getCount() >= MAX_RESULTS_PER_CATEGORY) {
                teamsHeader.showMoreButton(true);
                teamsHeader.setMoreCount(teamQueryResults.getCount() - MAX_RESULTS_PER_CATEGORY);

            } else {
                teamsHeader.showMoreButton(false);
            }
            listItems.add(teamsHeader);
            while (teamQueryResults.moveToNext()) {
                // Limit ourselves to a certain number of teams
                if (teamQueryResults.getPosition() >= MAX_RESULTS_PER_CATEGORY) {
                    break;
                }
                String key = teamQueryResults.getString(teamQueryResults.getColumnIndex(Database.SearchTeam.KEY));
                Team team = Database.getInstance(this).getTeam(key);
                TeamListElement element = new TeamListElement(team);
                listItems.add(element);
                Log.d(Constants.LOG_TAG, "titles: " + teamQueryResults.getString(teamQueryResults.getColumnIndex(Database.SearchTeam.TITLES)));
            }
        } else {
            teamsHeader = new SearchResultsHeaderListElement(getString(R.string.teams_header));
            teamsHeader.showMoreButton(false);
            listItems.add(teamsHeader);
            listItems.add(new EmptyListElement(getString(R.string.no_teams_found)));
        }

        // Events
        Cursor eventQueryResults = Database.getInstance(this).getMatchesForEventQuery(finalQuery);
        if (eventQueryResults != null && eventQueryResults.moveToFirst()) {
            eventQueryResults.moveToPosition(-1);

            eventsHeader = new SearchResultsHeaderListElement(getString(R.string.events_header));
            if (teamQueryResults.getCount() >= MAX_RESULTS_PER_CATEGORY) {
                eventsHeader.showMoreButton(true);
                eventsHeader.setMoreCount(teamQueryResults.getCount() - MAX_RESULTS_PER_CATEGORY);

            } else {
                eventsHeader.showMoreButton(false);
            }
            listItems.add(eventsHeader);

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
            eventsHeader = new SearchResultsHeaderListElement(getString(R.string.events_header));
            eventsHeader.showMoreButton(false);
            listItems.add(eventsHeader);
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
                } else if (clickedItem == teamsHeader) {
                    if (teamsHeader.isShowingMoreButton()) {
                        startActivity(MoreSearchResultsActivity.newInstance(SearchResultsActivity.this, MoreSearchResultsActivity.TEAM_RESULTS, query));
                    }
                } else if (clickedItem == eventsHeader) {
                    if (eventsHeader.isShowingMoreButton()) {
                        startActivity(MoreSearchResultsActivity.newInstance(SearchResultsActivity.this, MoreSearchResultsActivity.EVENT_RESULTS, query));
                    }
                }
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Search already handled by onQueryTextChange, return true.
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query.isEmpty()) {
            // If the user clears the search results, remove the adapter
            resultsList.setAdapter(null);
            return true;
        }
        updateQuery(query);
        return true;
    }
}
