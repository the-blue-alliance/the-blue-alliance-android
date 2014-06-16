package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

    SearchView searchView;

    int closeButtonId;

    private SearchResultsHeaderListElement teamsHeader, eventsHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        resultsList = (ListView) findViewById(R.id.results);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = new SearchView(getActionBar().getThemedContext());
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setQueryHint(getString(R.string.search_hint));
        // Prevent the "X" from iconifying the SearchView
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
        // The SearchView is empty; hide the close/clear button.
        // This will be shown once there is text in the field
        closeButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        searchView.findViewById(closeButtonId).setVisibility(View.GONE);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(searchView, layoutParams);
        int searchIconId = searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        // Hide the magnifying glass icon
        searchView.findViewById(searchIconId).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
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
                String key = teamQueryResults.getString(teamQueryResults.getColumnIndex(Database.SearchTeam.KEY));
                Team team = Database.getInstance(this).getTeam(key);
                TeamListElement element = new TeamListElement(team);
                listItems.add(element);
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
                String key = eventQueryResults.getString(eventQueryResults.getColumnIndex(Database.SearchEvent.KEY));
                Event event = Database.getInstance(this).getEvent(key);
                EventListElement element = new EventListElement(event);
                listItems.add(element);
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
                else {
                    searchView.setVisibility(View.INVISIBLE);
                    searchView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Search is already handled by onQueryTextChange,
        // but hide the soft keyboard regardless when the user hits the search button.
        // Also return true.
        searchView.setVisibility(View.INVISIBLE);
        searchView.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query.isEmpty()) {
            // If the user clears the search results, remove the adapter
            resultsList.setAdapter(null);
            // Hide the close button so the SearchView can't be iconified
            searchView.findViewById(closeButtonId).setVisibility(View.GONE);
            return true;
        } else {
            // Show the close button so the SearchView can be cleared
            searchView.findViewById(closeButtonId).setVisibility(View.VISIBLE);
            updateQuery(query);
            return true;
        }
    }
}
