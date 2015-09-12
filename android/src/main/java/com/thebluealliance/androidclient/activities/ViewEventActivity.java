package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.helpers.MyTBAHelper;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.views.SlidingTabs;

public class ViewEventActivity extends FABNotificationSettingsActivity
  implements ViewPager.OnPageChangeListener, HasFragmentComponent {

    public static final String EVENTKEY = "eventKey";
    public static final String TAB = "tab";

    private String mEventKey;
    private int currentTab;
    private TextView infoMessage;
    private TextView warningMessage;
    private ViewPager pager;
    private ViewEventFragmentPagerAdapter adapter;
    private boolean isDistrict;
    private FragmentComponent mComponent;

    /**
     * Create new intent for ViewEventActivity
     *
     * @param c        context
     * @param eventKey Key of the event to show
     * @param tab      The tab number from ViewEventFragmentPagerAdapter.
     * @return Intent you can launch
     */
    public static Intent newInstance(Context c, String eventKey, int tab) {
        Intent intent = new Intent(c, ViewEventActivity.class);
        intent.putExtra(EVENTKEY, eventKey);
        intent.putExtra(TAB, tab);
        return intent;
    }

    public static Intent newInstance(Context c, String eventKey) {
        return newInstance(c, eventKey, ViewEventFragmentPagerAdapter.TAB_INFO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // disable legacy RefreshableHostActivity
        setRefreshEnabled(false);

        MyTBAHelper.serializeIntent(getIntent());
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(EVENTKEY)) {
            mEventKey = getIntent().getExtras().getString(EVENTKEY, "");
        } else {
            throw new IllegalArgumentException("ViewEventActivity must be constructed with a key");
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TAB)) {
            currentTab = getIntent().getExtras().getInt(TAB, ViewEventFragmentPagerAdapter.TAB_INFO);
        } else {
            Log.i(Constants.LOG_TAG, "ViewEvent intent doesn't contain TAB. Defaulting to TAB_INFO");
            currentTab = ViewEventFragmentPagerAdapter.TAB_INFO;
        }

        setModelKey(mEventKey, ModelHelper.MODELS.EVENT);
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
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        pager.setCurrentItem(currentTab);  // Do this after we set onPageChangeListener, so that FAB gets hidden, if needed

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        isDistrict = true;

        setSettingsToolbarTitle("Event Settings");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getExtras() != null && intent.getExtras().containsKey(EVENTKEY)) {
            mEventKey = intent.getExtras().getString(EVENTKEY, "");
        } else {
            throw new IllegalArgumentException("ViewEventActivity must be constructed with a key");
        }
        setModelKey(mEventKey, ModelHelper.MODELS.EVENT);
        adapter = new ViewEventFragmentPagerAdapter(getSupportFragmentManager(), mEventKey);
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d(Constants.LOG_TAG, "Got new ViewEvent intent with key: " + mEventKey);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBeamUri(String.format(NfcUris.URI_EVENT, mEventKey));
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
        if (getSupportActionBar() == null) {
            return;
        }
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

    public void scrollToTab(int tab) {
        if (pager != null) {
            pager.setCurrentItem(tab);
        }
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
        currentTab = position;

        if (mOptionsMenu != null) {
            if (position == ViewEventFragmentPagerAdapter.TAB_STATS && !isDistrict) {
                showInfoMessage(getString(R.string.warning_not_real_district));
            } else {
                hideInfoMessage();
            }
        }

        // hide the FAB if we aren't on the first page
        if (position != ViewEventFragmentPagerAdapter.TAB_INFO) {
            hideFab(true);
        } else {
            showFab(true);
        }

        /* Track the call */
        /*Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, ViewEventActivity.this);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("view_event-tabs")
                .setAction("tab_change")
                .setLabel(eventKey+ " "+adapter.getPageTitle(position))
                .build());*/
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void onEvent(ActionBarTitleEvent event) {
        setActionBarTitle(event.getTitle());
    }

    public FragmentComponent getComponent() {
        if (mComponent == null) {
            TBAAndroid application = ((TBAAndroid) getApplication());
            mComponent = DaggerFragmentComponent.builder()
              .applicationComponent(application.getComponent())
              .datafeedModule(application.getDatafeedModule())
              .binderModule(application.getBinderModule())
              .databaseWriterModule(application.getDatabaseWriterModule())
              .subscriberModule(new SubscriberModule(this))
              .build();
        }
        return mComponent;
    }
}