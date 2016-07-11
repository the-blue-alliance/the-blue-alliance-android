package com.thebluealliance.androidclient.activities;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.AnalyticsActions;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.listitems.EmptyListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.SearchResultsHeaderListElement;
import com.thebluealliance.androidclient.listitems.TeamListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchResultsActivity extends NavigationDrawerActivity implements SearchView.OnQueryTextListener {

    private static final int MAX_RESULTS_PER_CATEGORY = 5;

    ListView resultsList;
    SearchView searchView;
    Toolbar toolbar;

    private SearchResultsHeaderListElement teamsHeader, eventsHeader;
    private String currentQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(toolbar);

        /* Report activity start to Analytics */
        new AnalyticsActions.ReportActivityStart(this).run();

        currentQuery = "";

        resultsList = (ListView) findViewById(R.id.results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setQueryHint(getString(R.string.search_hint));

        // Hide the magnifying glass icon
        searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon).setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(searchView, layoutParams);

        // Check if we got a search as the intent
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        if (Intent.ACTION_SEARCH.equals(intent.getAction()) || intent.getAction().equals("com.google.android.gms.actions.SEARCH_ACTION")) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchView.setQuery(query, true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AnalyticsHelper.sendSearchUpdate(this, currentQuery);
    }

    @Override
    protected void onStop() {
        super.onStop();
        new AnalyticsActions.ReportActivityStop(this).run();
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
        currentQuery = query;

        String preparedQuery = Utilities.getPreparedQueryForSearch(query);

        ArrayList<ListItem> listItems = new ArrayList<>();

        // Teams
        Cursor teamQueryResults = Database.getInstance(this).getMatchesForTeamQuery(preparedQuery);
        if (teamQueryResults != null && teamQueryResults.moveToFirst()) {
            teamQueryResults.moveToPosition(-1);

            teamsHeader = new SearchResultsHeaderListElement(getString(R.string.teams_header));
            if (teamQueryResults.getCount() > MAX_RESULTS_PER_CATEGORY) {
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
                String key = teamQueryResults.getString(teamQueryResults.getColumnIndex("_id"));
                Team team = Database.getInstance(this).getTeamsTable().get(key);
                if (team == null) {
                    // Don't display models that don't exist anymore and delete them from search indexes
                    team = new Team();
                    team.setKey(key);
                    Database.getInstance(this).getTeamsTable().deleteSearchIndex(team);
                    continue;
                }
                try {
                    TeamListElement element;
                    element = new TeamListElement(team);
                    listItems.add(element);
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.e(Constants.LOG_TAG, "Can't add team search result item. Missing fields... "
                          + Arrays.toString(e.getStackTrace()));
                }
                Log.d(Constants.LOG_TAG, "titles: " + teamQueryResults.getString(teamQueryResults.getColumnIndex(Database.SearchTeam.TITLES)));
            }
            teamQueryResults.close();
        } else {
            teamsHeader = new SearchResultsHeaderListElement(getString(R.string.teams_header));
            teamsHeader.showMoreButton(false);
            listItems.add(teamsHeader);
            listItems.add(new EmptyListElement(getString(R.string.no_teams_found)));
        }

        // Events
        Cursor eventQueryResults = Database.getInstance(this).getMatchesForEventQuery(preparedQuery);
        if (eventQueryResults != null && eventQueryResults.moveToFirst()) {
            eventQueryResults.moveToPosition(-1);

            eventsHeader = new SearchResultsHeaderListElement(getString(R.string.events_header));
            if (eventQueryResults.getCount() > MAX_RESULTS_PER_CATEGORY) {
                eventsHeader.showMoreButton(true);
                eventsHeader.setMoreCount(eventQueryResults.getCount() - MAX_RESULTS_PER_CATEGORY);

            } else {
                eventsHeader.showMoreButton(false);
            }
            listItems.add(eventsHeader);

            while (eventQueryResults.moveToNext()) {
                // Limit ourselves to a certain number of events
                if (eventQueryResults.getPosition() >= MAX_RESULTS_PER_CATEGORY) {
                    break;
                }
                String key = eventQueryResults.getString(eventQueryResults.getColumnIndex("_id"));
                Event event = Database.getInstance(this).getEventsTable().get(key);
                if (event == null) {
                    // Don't display models that don't exist anymore and delete them from search indexes
                    event = new Event();
                    event.setKey(key);
                    Database.getInstance(this).getEventsTable().deleteSearchIndex(event);
                    continue;
                }
                try {
                    EventListElement element;
                    element = new EventListElement(event);
                    listItems.add(element);
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.e(Constants.LOG_TAG, "Can't add event search result with missing fields...\n"
                            + Arrays.toString(e.getStackTrace()));
                }
            }
            eventQueryResults.close();
        } else {
            eventsHeader = new SearchResultsHeaderListElement(getString(R.string.events_header));
            eventsHeader.showMoreButton(false);
            listItems.add(eventsHeader);
            listItems.add(new EmptyListElement(getString(R.string.no_events_found)));
        }

        ListViewAdapter adapter = new ListViewAdapter(this, listItems);
        resultsList.setAdapter(adapter);

        resultsList.setOnItemClickListener((adapterView, view, position, id) -> {
            ListViewAdapter adapter1 = (ListViewAdapter) adapterView.getAdapter();
            ListItem clickedItem = adapter1.getItem(position);
            if (clickedItem instanceof TeamListElement) {
                String teamKey = ((ListElement) clickedItem).getKey();
                startActivity(ViewTeamActivity.newInstance(SearchResultsActivity.this, teamKey));
            } else if (clickedItem instanceof EventListElement) {
                String eventKey = ((ListElement) clickedItem).getKey();
                startActivity(ViewEventActivity.newInstance(SearchResultsActivity.this, eventKey));
            } else if (clickedItem == teamsHeader) {
                if (teamsHeader.isShowingMoreButton()) {
                    startActivity(MoreSearchResultsActivity.newInstance(SearchResultsActivity.this, MoreSearchResultsActivity.TEAM_RESULTS, query));
                }
            } else if (clickedItem == eventsHeader) {
                if (eventsHeader.isShowingMoreButton()) {
                    startActivity(MoreSearchResultsActivity.newInstance(SearchResultsActivity.this, MoreSearchResultsActivity.EVENT_RESULTS, query));
                }
            } else {
                searchView.setVisibility(View.INVISIBLE);
                searchView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Search is already handled by onQueryTextChange,
        // but hide the soft keyboard regardless when the user hits the search button.
        // Also return true.
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query.isEmpty()) {
            // If the user clears the search results, remove the adapter
            resultsList.setAdapter(null);
            return true;
        } else {
            updateQuery(query);
            return true;
        }
    }
}
