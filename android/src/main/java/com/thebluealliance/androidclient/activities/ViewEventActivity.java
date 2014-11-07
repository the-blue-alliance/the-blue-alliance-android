package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.views.SlidingTabs;

/**
 * File created by phil on 4/20/14.
 */
public class ViewEventActivity extends FABNotificationSettingsActivity implements ViewPager.OnPageChangeListener {

    public static final String EVENTKEY = "eventKey";

    private String mEventKey;
    private TextView infoMessage;
    private TextView warningMessage;
    private ViewPager pager;
    private ViewEventFragmentPagerAdapter adapter;
    private boolean isDistrict;

    public static Intent newInstance(Context c, String eventKey) {
        Intent intent = new Intent(c, ViewEventActivity.class);
        intent.putExtra(EVENTKEY, eventKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EVENTKEY)) {
            mEventKey = getIntent().getExtras().getString(EVENTKEY, "");
        } else {
            throw new IllegalArgumentException("ViewEventActivity must be constructed with a key");
        }

        setModelKey(mEventKey);
        setContentView(R.layout.activity_view_event);

        infoMessage = (TextView) findViewById(R.id.info_container);
        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideInfoMessage();
        hideWarningMessage();

        pager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new ViewEventFragmentPagerAdapter(getSupportFragmentManager(), mEventKey);
        pager.setAdapter(adapter);
        // To support refreshing, all pages must be held in memory at once
        // This should be increased if we ever add more pages
        pager.setOffscreenPageLimit(10);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setOnPageChangeListener(this);
        tabs.setViewPager(pager);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        setBeamUri(String.format(NfcUris.URI_EVENT, mEventKey));
        isDistrict = true;
    }

    public void updateDistrict(boolean isDistrict) {
        this.isDistrict = isDistrict;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mOptionsMenu = menu;
        return true;
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
        encourageLearning(false);
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // The title is empty now; the EventInfoFragment will set the appropriate title
        // once it is loaded.
        setActionBarTitle("");
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
                // If this tasks exists in the back stack, it will be brought to the front and all other activities
                // will be destroyed. HomeActivity will be delivered this intent via onNewIntent().
                startActivity(HomeActivity.newInstance(this, R.id.nav_item_events).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            case R.id.stats_help:
                Utilities.showHelpDialog(this, R.raw.stats_help, getString(R.string.stats_help_title));
                return true;
            case R.id.points_help:
                Utilities.showHelpDialog(this, R.raw.district_points_help, getString(R.string.district_points_help));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ViewPager getPager() {
        return pager;
    }

    public void showInfoMessage(String message) {
        infoMessage.setText(message);
        infoMessage.setVisibility(View.VISIBLE);
    }

    public void hideInfoMessage() {
        infoMessage.setVisibility(View.GONE);
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mOptionsMenu != null) {
            if (position == 5 && !isDistrict) {
                showInfoMessage(getString(R.string.warning_not_real_district));
            } else {
                hideInfoMessage();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}