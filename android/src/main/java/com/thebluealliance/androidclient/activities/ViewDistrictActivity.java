package com.thebluealliance.androidclient.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ViewDistrictFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.DistrictHelper;

/**
 * Created by phil on 7/10/14.
 */
public class ViewDistrictActivity extends RefreshableHostActivity {

    public static final String DISTRICT_ABBREV = "districtKey";
    public static final String YEAR = "year";

    private String districtAbbrev, districtKey;
    private int year;
    private TextView warningMessage;
    private ViewPager pager;
    private ViewDistrictFragmentPagerAdapter adapter;

    public static Intent newInstance(Context c, String districtAbbrev, int year) {
        Intent intent = new Intent(c, ViewDistrictActivity.class);
        intent.putExtra(DISTRICT_ABBREV, districtAbbrev);
        intent.putExtra(YEAR, year);
        return intent;
    }

    public static Intent newInstance(Context c, String districtKey){
        int year = Integer.parseInt(districtKey.substring(0, 4));
        String abbrev = districtKey.substring(4);
        Intent intent = new Intent(c, ViewDistrictActivity.class);
        intent.putExtra(DISTRICT_ABBREV, abbrev);
        intent.putExtra(YEAR, year);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_district);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(DISTRICT_ABBREV)) {
            districtAbbrev = getIntent().getExtras().getString(DISTRICT_ABBREV, "");
        } else {
            throw new IllegalArgumentException("ViewDistrictActivity must be constructed with a key");
        }
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(YEAR)) {
            year = getIntent().getExtras().getInt(YEAR, -1);
        } else {
            throw new IllegalArgumentException("ViewDistrictActivity must be constructed with a year");
        }

        districtKey = DistrictHelper.generateKey(districtAbbrev, year);

        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        pager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new ViewDistrictFragmentPagerAdapter(getSupportFragmentManager(), districtKey);
        pager.setAdapter(adapter);
        // To support refreshing, all pages must be held in memory at once
        // This should be increased if we ever add more pages
        pager.setOffscreenPageLimit(10);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        setBeamUri(String.format(NfcUris.URI_DISTRICT, districtAbbrev));
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
        encourageLearning(false);
    }

    private void setupActionBar() {
        ActionBar bar = getActionBar();
        if(bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            setActionBarTitle(year + " " + DistrictHelper.districtTypeFromKey(districtKey).getName());
        }
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
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_events)).startActivities();
                } else {
                    Log.d(Constants.LOG_TAG, "Navigating up...");
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
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
