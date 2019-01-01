package com.thebluealliance.androidclient.background;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;

public class RecreateSearchIndexes extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_RECREATE_SEARCH = "com.thebluealliance.androidclient.background.action.RECREATE_SEARCH";

    @Inject Database mDb;

    @Override
    public void onCreate() {
        super.onCreate();
        ((TBAAndroid)getApplication()).getDbComponent().inject(this);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If the service is
     * already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionRecreateSearchIndexes(Context context) {
        Intent intent = new Intent(context, RecreateSearchIndexes.class);
        intent.setAction(ACTION_RECREATE_SEARCH);
        context.startService(intent);
    }

    public RecreateSearchIndexes() {
        super("RecreateSearchIndexes");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RECREATE_SEARCH.equals(action)) {
                TbaLogger.d("Recreating search indexes");
                recreateSearchIndexes();
            }
        }
    }

    /**
     * Handle action in the provided background thread with the provided parameters.
     */
    private void recreateSearchIndexes() {
        // Get current events and teams to create indexes for
        List<Event> events = mDb.getEventsTable().getAll();
        List<Team> teams = mDb.getTeamsTable().getAll();
        TbaLogger.d("Saving " + events.size() + " events and " + teams.size() + "teams");

        // remove current indexes
        mDb.getTeamsTable().deleteAllSearchIndexes();
        mDb.getEventsTable().deleteAllSearchIndexes();

        // store new indexes
        mDb.getEventsTable().recreateAllSearchIndexes(events);
        mDb.getTeamsTable().recreateAllSearchIndexes(teams);
        TbaLogger.d("New indexes inserted");
    }
}
