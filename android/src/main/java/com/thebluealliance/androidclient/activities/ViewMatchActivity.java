package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.ShareUris;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ViewMatchFragmentPagerAdapter;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.views.SlidingTabs;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ViewMatchActivity extends MyTBASettingsActivity
  implements ViewPager.OnPageChangeListener {

    public static final String MATCH_KEY = "match_key";
    public static final String TAB = "tab";

    private String mMatchKey;
    private int currentTab;
    private ViewPager pager;
    private ViewMatchFragmentPagerAdapter adapter;

    public static Intent newInstance(Context context, String matchKey) {
        return newInstance(context, matchKey, ViewMatchFragmentPagerAdapter.TAB_RESULT);
    }

    public static Intent newInstance(Context context, String matchKey, int tab) {
        Intent intent = new Intent(context, ViewMatchActivity.class);
        intent.putExtra(MATCH_KEY, matchKey);
        intent.putExtra(TAB, tab);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMatchKey = getIntent().getStringExtra(MATCH_KEY);
        if (mMatchKey == null) {
            throw new IllegalArgumentException("ViewMatchActivity must be created with a match key!");
        }
        setModelKey(mMatchKey, ModelType.MATCH);
        setShareEnabled(true);
        setContentView(R.layout.activity_view_match);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(toolbar);
        setupActionBar();
        setSettingsToolbarTitle("Match settings");

        currentTab = getIntent().getIntExtra(TAB, ViewMatchFragmentPagerAdapter.TAB_RESULT);

        pager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new ViewMatchFragmentPagerAdapter(getResources(), getSupportFragmentManager(),
                mMatchKey);
        pager.setAdapter(adapter);
        // To support refreshing, all pages must be held in memory at once
        // This should be increased if we ever add more pages
        pager.setOffscreenPageLimit(10);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(this);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        pager.setCurrentItem(currentTab);  // Do this after we set onPageChangeListener, so that FAB gets hidden, if needed

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(BaseActivity.WARNING_OFFLINE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mMatchKey = intent.getStringExtra(MATCH_KEY);
        if (mMatchKey == null) {
            throw new IllegalArgumentException("ViewMatchActivity must be created with a match key!");
        }
        setModelKey(mMatchKey, ModelType.MATCH);
        TbaLogger.i("New ViewMatch intent with key: " + mMatchKey);
        setupActionBar();

        currentTab = getIntent().getIntExtra(TAB, ViewMatchFragmentPagerAdapter.TAB_RESULT);

        pager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new ViewMatchFragmentPagerAdapter(getResources(), getSupportFragmentManager(),
                mMatchKey);
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setShareUri(String.format(ShareUris.URI_MATCH, mMatchKey));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_match_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String eventKey = MatchHelper.getEventKeyFromMatchKey(mMatchKey);

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (isDrawerOpen()) {
                    closeDrawer();
                    return true;
                }

                Intent upIntent = ViewEventActivity.newInstance(this, eventKey);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TbaLogger.d("Navigating to new back stack with key " + eventKey);
                    TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_events))
                            .addNextIntent(ViewEventActivity.newInstance(this, eventKey)).startActivities();
                } else {
                    TbaLogger.d("Navigating up...");
                    upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(upIntent);
                    finish();
                }
                return true;
            case R.id.action_view_event:
                startActivity(ViewEventActivity.newInstance(this, eventKey));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionBarTitleUpdated(ActionBarTitleEvent event) {
        setActionBarTitle(event.getTitle());
        setActionBarSubtitle(event.getSubtitle());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentTab = position;

        // hide the FAB if we aren't on the first page
        if (position != ViewMatchFragmentPagerAdapter.TAB_RESULT) {
            hideFab(true);
        } else {
            showFab(true);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
