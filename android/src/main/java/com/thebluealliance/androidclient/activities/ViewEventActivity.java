package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;

/**
 * File created by phil on 4/20/14.
 */
public class ViewEventActivity extends BaseActivity {

    private String mEventKey;
    private TextView warningMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        if (getIntent().getExtras() != null) {
            mEventKey = getIntent().getExtras().getString("eventKey", "");
        }

        warningMessage = (TextView)findViewById(R.id.warning_container);
        hideWarningMessage();

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new ViewEventFragmentPagerAdapter(getSupportFragmentManager(), mEventKey));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

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
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
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
