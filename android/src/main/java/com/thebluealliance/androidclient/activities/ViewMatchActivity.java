package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.match.PopulateMatchInfo;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * Created by Nathan on 5/14/2014.
 */
public class ViewMatchActivity extends FABNotificationSettingsActivity implements RefreshListener {

    public static final String MATCH_KEY = "match_key";

    private String mMatchKey;

    private TextView warningMessage;

    private PopulateMatchInfo task;

    public static Intent newInstance(Context context, String matchKey) {
        Intent intent = new Intent(context, ViewMatchActivity.class);
        intent.putExtra(MATCH_KEY, matchKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMatchKey = getIntent().getStringExtra(MATCH_KEY);
        if (mMatchKey == null) {
            throw new IllegalArgumentException("ViewMatchActivity must be created with a match key!");
        }
        setModelKey(mMatchKey, ModelHelper.MODELS.MATCH);
        setContentView(R.layout.activity_view_match);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        setSettingsToolbarTitle("Match settings");

        warningMessage = (TextView) findViewById(R.id.warning_container);

        registerRefreshListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mMatchKey = intent.getStringExtra(MATCH_KEY);
        if (mMatchKey == null) {
            throw new IllegalArgumentException("ViewMatchActivity must be created with a match key!");
        }
        setModelKey(mMatchKey, ModelHelper.MODELS.MATCH);
        Log.i(Constants.LOG_TAG, "New ViewMatch intent with key: " + mMatchKey);
        setupActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBeamUri(String.format(NfcUris.URI_MATCH, mMatchKey));

        startRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task != null) {
            task.cancel(false);
        }
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("");
    }

    @Override
    public void onRefreshStart(boolean actionItemPressed) {
        Log.i(Constants.REFRESH_LOG, "Match " + mMatchKey + " refresh started");
        task = new PopulateMatchInfo(this, new RequestParams(true, actionItemPressed));
        task.execute(mMatchKey);
        // Indicate loading; the task will hide the progressbar and show the content when loading is complete
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefreshStop() {
        task.cancel(false);
    }

    @Override
    public void showWarningMessage(String message) {
        if (warningMessage != null) {
            warningMessage.setText(message);
            warningMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideWarningMessage() {
        if (warningMessage != null) {
            warningMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_match_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (isDrawerOpen()) {
                    closeDrawer();
                    return true;
                }
                String eventKey = mMatchKey.substring(0, mMatchKey.indexOf("_"));
                Intent upIntent = ViewEventActivity.newInstance(this, eventKey);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    Log.d(Constants.LOG_TAG, "Navigating to new back stack with key " + eventKey);
                    TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_events))
                            .addNextIntent(ViewEventActivity.newInstance(this, eventKey)).startActivities();
                } else {
                    Log.d(Constants.LOG_TAG, "Navigating up...");
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.action_view_event:
                startActivity(ViewEventActivity.newInstance(this, mMatchKey.split("_")[0]));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
