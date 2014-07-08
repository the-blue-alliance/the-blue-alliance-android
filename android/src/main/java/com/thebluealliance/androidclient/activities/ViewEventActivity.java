package com.thebluealliance.androidclient.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * File created by phil on 4/20/14.
 */
public class ViewEventActivity extends RefreshableHostActivity implements ViewPager.OnPageChangeListener {

    public static final String EVENTKEY = "eventKey";

    private String mEventKey;
    private TextView warningMessage;
    private ViewPager pager;

    public static Intent newInstance(Context c, String eventKey) {
        Intent intent = new Intent(c, ViewEventActivity.class);
        intent.putExtra(EVENTKEY, eventKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EVENTKEY)) {
            mEventKey = getIntent().getExtras().getString(EVENTKEY, "");
        } else {
            throw new IllegalArgumentException("ViewEventActivity must be constructed with a key");
        }

        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new ViewEventFragmentPagerAdapter(getSupportFragmentManager(), mEventKey));
        // To support refreshing, all pages must be held in memory at once
        // This should be increased if we ever add more pages
        pager.setOffscreenPageLimit(5);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setOnPageChangeListener(this);
        tabs.setViewPager(pager);

        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        setBeamUri(String.format(NfcUris.URI_EVENT, mEventKey));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.stats_help_menu, menu);
        mOptionsMenu = menu;
        mOptionsMenu.findItem(R.id.help).setVisible(false);
        return true;
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
        encourageLearning(false);
    }

    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_events)).startActivities();
                } else {
                    Log.d(Constants.LOG_TAG, "Navigating up...");
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.help:
                showStatsHelpDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showStatsHelpDialog() {
        String helpText;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.stats_help)));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
                line = br.readLine();
            }
            helpText = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            helpText = "Error reading help file.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.stats_help_title));
        builder.setMessage(Html.fromHtml(helpText));
        builder.setCancelable(true);
        builder.setNeutralButton(getString(R.string.close_stats_help),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        builder.create().show();
    }

    public ViewPager getPager() {
        return pager;
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
        if (position == 4) {
            //stats position
            mOptionsMenu.findItem(R.id.help).setVisible(true);
        } else {
            mOptionsMenu.findItem(R.id.help).setVisible(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
