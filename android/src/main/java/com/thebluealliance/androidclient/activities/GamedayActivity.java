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
  implements ViewPager.OnPageChangeListener, HasFragmentComponent {

    public static final String TAB = "tab";

    private static final int FAB_ANIMATION_DURATION = 250;

    private FragmentComponent mComponent;
    private TextView mWarningMessage;

    FloatingActionButton mFab;
    boolean mIsFabVisible = true;
    ValueAnimator mRunningFabAnimation;

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

        mFab = (FloatingActionButton) findViewById(R.id.filter_button);

        mWarningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        GamedayFragmentPagerAdapter adapter = new GamedayFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));
        pager.setCurrentItem(currentTab);
        pager.setOnPageChangeListener(this);


        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(this);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }
    }

    @Override
    public void onNavigationDrawerCreated() {
        // This isn't automagically handled because we're in a different activity. Set it manually.
        setNavigationDrawerItemSelected(R.id.nav_item_gameday);
    }

    public FloatingActionButton getmFab() {
        return mFab;
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

    @Override
    public void showWarningMessage(CharSequence warningMessage) {
        mWarningMessage.setText(warningMessage);
        mWarningMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWarningMessage() {
        mWarningMessage.setVisibility(View.GONE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("tba", "gameday page selected: " + position);
        if (position == 0) {
            // The gameday ticker tab was selected. Show the FAB.
            showFab();
        } else {
            // We scrolled to a different tab. Hide the FAB.
            hideFab();

        }
    }

    private void showFab() {
        if (mIsFabVisible) {
            return;
        }
        mIsFabVisible = true;
        if (mRunningFabAnimation != null) {
            mRunningFabAnimation.cancel();
        }

        ValueAnimator fabScaleUp = ValueAnimator.ofFloat(0, 1);
        fabScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFab.setVisibility(View.VISIBLE);
            }
        });
        fabScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mFab, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mFab, (float) animation.getAnimatedValue());
        });
        fabScaleUp.setDuration(FAB_ANIMATION_DURATION);
        fabScaleUp.setInterpolator(new DecelerateInterpolator());
        fabScaleUp.start();
        mRunningFabAnimation = fabScaleUp;
    }

    private void hideFab() {
        if (!mIsFabVisible) {
            return;
        }
        mIsFabVisible = false;
        if (mRunningFabAnimation != null) {
            mRunningFabAnimation.cancel();
        }

        ValueAnimator fabScaleDown = ValueAnimator.ofFloat(1, 0);
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFab.setVisibility(View.VISIBLE);
            }
        });
        fabScaleDown.addUpdateListener(animation -> {
            ViewCompat.setScaleX(mFab, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(mFab, (float) animation.getAnimatedValue());
        });
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFab.setVisibility(View.GONE);
            }
        });
        fabScaleDown.setDuration(FAB_ANIMATION_DURATION);
        fabScaleDown.setInterpolator(new AccelerateInterpolator());
        fabScaleDown.start();
        mRunningFabAnimation = fabScaleDown;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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