package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.match.PopulateMatchInfo;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * Created by Nathan on 5/14/2014.
 */
public class ViewMatchActivity extends RefreshableHostActivity implements RefreshListener {

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
        setContentView(R.layout.activity_view_match);
        setupActionBar();

        mMatchKey = getIntent().getStringExtra(MATCH_KEY);
        if (mMatchKey == null) {
            throw new IllegalArgumentException("ViewMatchActivity must be created with a match key!");
        }

        warningMessage = (TextView) findViewById(R.id.warning_container);

        registerRefreshableActivityListener(this);

        setBeamUri(String.format(NfcUris.URI_MATCH, mMatchKey));
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
    }

    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("Match");
    }

    @Override
    public void onResume() {
        super.onResume();
        startRefresh();
    }

    @Override
    public void onRefreshStart() {
        Log.i(Constants.REFRESH_LOG, "Match " + mMatchKey + " refresh started");
        task = new PopulateMatchInfo(this, true);
        task.execute(mMatchKey);
        // Indicate loading; the task will hide the progressbar and show the content when loading is complete
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        findViewById(R.id.match_container).setVisibility(View.GONE);
    }

    @Override
    public void onRefreshStop() {
        task.cancel(false);
    }

    @Override
    public void showWarningMessage(String message) {
        warningMessage.setText(message);
        warningMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWarningMessage() {
        warningMessage.setVisibility(View.GONE);
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
        switch (id){
            case android.R.id.home:
                if(isDrawerOpen()) {
                    closeDrawer();
                    return true;
                }
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                String eventKey = mMatchKey.substring(0, mMatchKey.indexOf("_"));
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    Log.d(Constants.LOG_TAG, "Navgating to new back stack with key " + eventKey);
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
