package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;

/**
 * File created by nathan on 4/21/14.
 */
public class ViewTeamActivity extends BaseActivity {

    public static final String TEAM_KEY = "team_key";
    private TextView warningMessage;

    // Should come in the format frc####
    private String mTeamKey;

    public static Intent newInstance(Context context, String teamKey){
        System.out.println("making intent for "+teamKey);
        Intent intent = new Intent(context, ViewTeamActivity.class);
        intent.putExtra(TEAM_KEY, teamKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_team);

        mTeamKey = getIntent().getStringExtra(TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("ViewTeamActivity must be created with a team key!");
        }

        warningMessage = (TextView)findViewById(R.id.warning_container);
        hideWarningMessage();

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new ViewTeamFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        // Setup the action bar
        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectionDetector.isConnectedToInternet(this)) {
            hideWarningMessage();
        }else{
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }
    }

    private void setupActionBar() {
        ActionBar bar = getActionBar();
        if (bar != null) {
            // Setup the title
            String teamNumber = mTeamKey.replace("frc", "");
            getActionBar().setTitle("Team " + teamNumber);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
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
}
