package com.thebluealliance.androidclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.GamedayFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.views.SlidingTabs;

public class GamedayActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    public static final String TAB = "tab";

    private static final int FAB_ANIMATE_DURATION = 250;

    private TextView warningMessage;
    private int currentTab;
    private GamedayFragmentPagerAdapter adapter;
    private ViewPager pager;

    FloatingActionButton fab;
    View fabContainer;
    boolean fabVisible = true;
    ValueAnimator runningFabAnimation;

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

        fab = (FloatingActionButton) findViewById(R.id.filter_button);
        fabContainer = findViewById(R.id.filter_button_container);

        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        pager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new GamedayFragmentPagerAdapter(getSupportFragmentManager());
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

    public FloatingActionButton getFab() {
        return fab;
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
        if(fabVisible) {
            return;
        }
        fabVisible = true;
        if(runningFabAnimation != null) {
            runningFabAnimation.cancel();
        }

        ValueAnimator fabScaleUp = ValueAnimator.ofFloat(0, 1);
        fabScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fabContainer.setVisibility(View.VISIBLE);
            }
        });
        fabScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(fab, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(fab, (float) animation.getAnimatedValue());
        });
        fabScaleUp.setDuration(FAB_ANIMATE_DURATION);
        fabScaleUp.setInterpolator(new DecelerateInterpolator());
        fabScaleUp.start();
        runningFabAnimation = fabScaleUp;
    }

    private void hideFab() {
        if(!fabVisible) {
            return;
        }
        fabVisible = false;
        if(runningFabAnimation != null) {
            runningFabAnimation.cancel();
        }

        ValueAnimator fabScaleDown = ValueAnimator.ofFloat(1, 0);
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fabContainer.setVisibility(View.VISIBLE);
            }
        });
        fabScaleDown.addUpdateListener(animation -> {
            ViewCompat.setScaleX(fab, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(fab, (float) animation.getAnimatedValue());
        });
        fabScaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabContainer.setVisibility(View.GONE);
            }
        });
        fabScaleDown.setDuration(FAB_ANIMATE_DURATION);
        fabScaleDown.setInterpolator(new AccelerateInterpolator());
        fabScaleDown.start();
        runningFabAnimation = fabScaleDown;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
