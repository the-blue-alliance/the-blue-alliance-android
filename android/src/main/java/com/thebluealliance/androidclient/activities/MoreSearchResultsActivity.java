package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.EventCursorAdapter;
import com.thebluealliance.androidclient.adapters.SimpleCursorLoader;
import com.thebluealliance.androidclient.adapters.TeamCursorAdapter;
import com.thebluealliance.androidclient.background.AnalyticsActions;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MoreSearchResultsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TEAM_RESULTS = 1;
    public static final int EVENT_RESULTS = 2;

    public static final String RESULTS_TYPE = "results_type";
    public static final String QUERY = "query";
    private static final String PREPARED_QUERY = "preparedQuery";

    private ListView resultsList;
    private Toolbar toolbar;
    private String query;

    private int resultsType;

    @Inject Database mDb;

    public static Intent newInstance(Context c, int mode, String query) {
        Intent i = new Intent(c, MoreSearchResultsActivity.class);
        i.putExtra(RESULTS_TYPE, mode);
        i.putExtra(QUERY, query);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.configureActivityForEdgeToEdge(this);
        setContentView(R.layout.activity_search_results);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Report activity start to Analytics */
        new AnalyticsActions.ReportActivityStart(this).run();

        resultsList = (ListView) findViewById(R.id.results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        query = getIntent().getStringExtra(QUERY);
        resultsType = getIntent().getIntExtra(RESULTS_TYPE, -1);
        if (query == null || resultsType == -1) {
            throw new IllegalArgumentException("MoreSearchResultsActivity most be created with a mode and query string!");
        }

        String preparedQuery = Utilities.getPreparedQueryForSearch(query);

        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(PREPARED_QUERY, preparedQuery);

        getSupportLoaderManager().restartLoader(resultsType, loaderBundle, this);
        switch (resultsType) {
            case TEAM_RESULTS:
                getSupportActionBar().setTitle(String.format(getString(R.string.teams_matching), query));
                break;
            case EVENT_RESULTS:
                getSupportActionBar().setTitle(String.format(getString(R.string.events_matching), query));
                break;
        }

        resultsList.setOnItemClickListener((adapterView, view, position, id) -> {
            switch (resultsType) {
                case TEAM_RESULTS:
                    TeamCursorAdapter teamAdapter = (TeamCursorAdapter) adapterView.getAdapter();
                    String teamKey = teamAdapter.getKey(position);
                    startActivity(ViewTeamActivity.newInstance(MoreSearchResultsActivity.this, teamKey));
                    break;
                case EVENT_RESULTS:
                    EventCursorAdapter eventAdapter = (EventCursorAdapter) adapterView.getAdapter();
                    String eventKey = eventAdapter.getKey(position);
                    startActivity(ViewEventActivity.newInstance(MoreSearchResultsActivity.this, eventKey));
                    break;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        AnalyticsHelper.sendSearchUpdate(this, query);
        query = "";
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        final String preparedQuery = bundle.getString(PREPARED_QUERY);
        switch (i) {
            case TEAM_RESULTS:
                return new SimpleCursorLoader(MoreSearchResultsActivity.this) {
                    @Override
                    public Cursor loadInBackground() {
                        return mDb.getTeamsTable().getForSearchQuery(preparedQuery);
                    }
                };
            case EVENT_RESULTS:
                return new SimpleCursorLoader(MoreSearchResultsActivity.this) {
                    @Override
                    public Cursor loadInBackground() {
                        return mDb.getEventsTable().getForSearchQuery(preparedQuery);
                    }
                };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (resultsType) {
            case TEAM_RESULTS:
                resultsList.setAdapter(new TeamCursorAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
                break;
            case EVENT_RESULTS:
                resultsList.setAdapter(new EventCursorAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        resultsList.setAdapter(null);
    }
}
