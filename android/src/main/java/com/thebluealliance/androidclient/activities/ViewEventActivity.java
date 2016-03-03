package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.models.APIStatus;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.views.SlidingTabs;

public class ViewEventActivity extends MyTBASettingsActivity
        implements ViewPager.OnPageChangeListener, HasFragmentComponent {

    public static final String EVENTKEY = "eventKey";
    public static final String TAB = "tab";

    private String mEventKey;
    private int mSelectedTab;
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

        Bundle extras = getIntent().getExtras() == null ? new Bundle() : getIntent().getExtras();

        mEventKey = extras.getString(EVENTKEY, "");
        if (!EventHelper.validateEventKey(mEventKey)) {
            throw new IllegalArgumentException("ViewEventActivity must be given a valid event key");
        }

        mSelectedTab = extras.getInt(TAB, ViewEventFragmentPagerAdapter.TAB_INFO);

        setModelKey(mEventKey, ModelType.EVENT);
        setContentView(R.layout.activity_view_event);

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

        pager.setCurrentItem(mSelectedTab);  // Do this after we set onPageChangeListener, so that FAB gets hidden, if needed

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(BaseActivity.WARNING_OFFLINE);
        }

        isDistrict = true;

        setSettingsToolbarTitle("Event Settings");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(Constants.LOG_TAG, "onNewIntent called");
        setIntent(intent);
        String newEventKey;

        Bundle extras = intent.getExtras() == null ? new Bundle() : intent.getExtras();

        newEventKey = extras.getString(EVENTKEY, "");
        if (!EventHelper.validateEventKey(newEventKey)) {
            throw new IllegalArgumentException("ViewEventActivity must be constructed with a key");
        }

        mSelectedTab = extras.getInt(TAB, ViewEventFragmentPagerAdapter.TAB_INFO);

        if (mEventKey != null && newEventKey.equals(mEventKey)) {
            // The event keys are the same; don't recreate anything
            return;
        } else {
            mEventKey = newEventKey;
        }
        setModelKey(mEventKey, ModelType.EVENT);

        // If the settings panel was open before, close it
        closeSettingsPanel();

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

    @Override
    protected void onTbaStatusUpdate(APIStatus newStatus) {
        super.onTbaStatusUpdate(newStatus);
        if (newStatus.getDownEvents().contains(mEventKey)) {
            // This event is down
            showWarningMessage(BaseActivity.WARNING_EVENT_DOWN);
        } else {
            // This event is not down! Hide the message if it was previously displayed
            dismissWarningMessage(BaseActivity.WARNING_EVENT_DOWN);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mSelectedTab = position;

        // hide the FAB if we aren't on the first page
        if (position != ViewEventFragmentPagerAdapter.TAB_INFO) {
            hideFab(true);
        } else {
            showFab(true, false);
        }
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
                    .clickListenerModule(new ClickListenerModule(this))
                    .build();
        }
        return mComponent;
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }
}