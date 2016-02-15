package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.GamedayFragmentPagerAdapter;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;
import com.thebluealliance.androidclient.views.SlidingTabs;

public class GamedayActivity extends BaseActivity
  implements HasFragmentComponent {

    public static final String TAB = "tab";

    private FragmentComponent mComponent;

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

        int currentTab;
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TAB)) {
            currentTab = getIntent().getExtras().getInt(TAB, GamedayFragmentPagerAdapter.TAB_TICKER);
        } else {
            Log.i(Constants.LOG_TAG, "GameDayActivity intent doesn't contain TAB. Defaulting to TAB_TICKER");
            currentTab = GamedayFragmentPagerAdapter.TAB_TICKER;
        }

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        GamedayFragmentPagerAdapter adapter = new GamedayFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));
        pager.setCurrentItem(currentTab);

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(BaseActivity.WARNING_OFFLINE);
        }
    }

    @Override
    public void onNavigationDrawerCreated() {
        // This isn't automagically handled because we're in a different activity. Set it manually.
        setNavigationDrawerItemSelected(R.id.nav_item_gameday);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBeamUri(NfcUris.URI_GAMEDAY);
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle(R.string.title_activity_gameday);
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
}