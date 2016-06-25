package com.thebluealliance.androidclient.background;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class RecreateSearchIndexes extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_RECREATE_SEARCH = "com.thebluealliance.androidclient.background.action.RECREATE_SEARCH";

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
                Log.d(Constants.LOG_TAG, "Recreating search indexes");
                recreateSearchIndexes();
            }
        }
    }

    /**
     * Handle action in the provided background thread with the provided parameters.
     */
    private void recreateSearchIndexes() {
        Database db = Database.getInstance(this);

        // Get current events and teams to create indexes for
        List<Event> events = db.getEventsTable().getAll();
        List<Team> teams = db.getTeamsTable().getAll();
        Log.d(Constants.LOG_TAG, "Saving " + events.size() + " events and " + teams.size() + "teams");

        // remove current indexes
        db.getTeamsTable().deleteAllSearchIndexes();
        db.getEventsTable().deleteAllSearchIndexes();

        // store new indexes
        db.getEventsTable().recreateAllSearchIndexes(events);
        db.getTeamsTable().recreateAllSearchIndexes(teams);
        Log.d(Constants.LOG_TAG, "New indexes inserted");
    }
}
