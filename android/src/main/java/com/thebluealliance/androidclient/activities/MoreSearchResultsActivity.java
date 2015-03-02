package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.EventCursorAdapter;
import com.thebluealliance.androidclient.adapters.SimpleCursorLoader;
import com.thebluealliance.androidclient.adapters.TeamCursorAdapter;
import com.thebluealliance.androidclient.background.AnalyticsActions;
import com.thebluealliance.androidclient.datafeed.Database;

/**
 * Created by Nathan on 6/15/2014.
 */
public class MoreSearchResultsActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TEAM_RESULTS = 1;
    public static final int EVENT_RESULTS = 2;

    public static final String RESULTS_TYPE = "results_type";
    public static final String QUERY = "query";
    private static final String PREPARED_QUERY = "preparedQuery";

    private ListView resultsList;
    private String query;

    private int resultsType;

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

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

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

        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, this);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("search")
                .setAction(query)
                .setLabel("search")
                .build());
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
                        return Database.getInstance(MoreSearchResultsActivity.this).getTeamsForTeamQuery(preparedQuery);
                    }
                };
            case EVENT_RESULTS:
                return new SimpleCursorLoader(MoreSearchResultsActivity.this) {
                    @Override
                    public Cursor loadInBackground() {
                        return Database.getInstance(MoreSearchResultsActivity.this).getEventsForQuery(preparedQuery);
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
