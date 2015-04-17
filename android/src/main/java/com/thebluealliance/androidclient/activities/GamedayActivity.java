package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.GamedayFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.views.SlidingTabs;

public class GamedayActivity extends BaseActivity {

    public static final String TAB = "tab";

    private TextView warningMessage;
    private int currentTab;
    private GamedayFragmentPagerAdapter adapter;
    private ViewPager pager;

    public static Intent newInstance(Context context) {
        return newInstance(context, GamedayFragmentPagerAdapter.TAB_TICKER);
    }

    public static Intent newInstance(Context context, int tab) {
        Intent intent = new Intent(context, GamedayActivity.class);
        intent.putExtra(TAB, tab);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameday);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TAB)) {
            currentTab = getIntent().getExtras().getInt(TAB, GamedayFragmentPagerAdapter.TAB_TICKER);
        } else {
            Log.i(Constants.LOG_TAG, "GameDayActivity intent doesn't contain TAB. Defaulting to TAB_TICKER");
            currentTab = GamedayFragmentPagerAdapter.TAB_TICKER;
        }

        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        pager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new GamedayFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));
        pager.setCurrentItem(currentTab);

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        Firebase.setAndroidContext(this);

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBeamUri(NfcUris.URI_GAMEDAY);
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("TBA GameDay"); //TODO move to string resource
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
