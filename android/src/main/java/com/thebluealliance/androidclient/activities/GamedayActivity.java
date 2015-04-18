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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.firebase.client.Firebase;
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

    private static final int ANIMATION_DURATION = 250;

    private TextView warningMessage;
    private int currentTab;
    private GamedayFragmentPagerAdapter adapter;
    private ViewPager pager;

    FloatingActionButton fab;
    View fabContainer;

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

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();

        Firebase.setAndroidContext(this);

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("tba", "gameday page selected: " + position);
        if(position == 0) {
            // The gameday ticker tab was selected. Show the FAB.
            showFab();
        } else {
            // We scrolled to a different tab. Hide the FAB.
            hideFab();

        }
    }

    private void showFab() {
        ValueAnimator closeButtonScaleUp = ValueAnimator.ofFloat(0, 1).setDuration(ANIMATION_DURATION);
        closeButtonScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fabContainer.setVisibility(View.VISIBLE);
            }
        });
        closeButtonScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(fab, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(fab, (float) animation.getAnimatedValue());
        });
        closeButtonScaleUp.setDuration(ANIMATION_DURATION);
        closeButtonScaleUp.setInterpolator(new DecelerateInterpolator());
        closeButtonScaleUp.start();
    }

    private void hideFab() {
        ValueAnimator closeButtonScaleUp = ValueAnimator.ofFloat(1, 0).setDuration(ANIMATION_DURATION);
        closeButtonScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fabContainer.setVisibility(View.VISIBLE);
            }
        });
        closeButtonScaleUp.addUpdateListener(animation -> {
            ViewCompat.setScaleX(fab, (float) animation.getAnimatedValue());
            ViewCompat.setScaleY(fab, (float) animation.getAnimatedValue());
        });
        closeButtonScaleUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fabContainer.setVisibility(View.GONE);
            }
        });
        closeButtonScaleUp.setDuration(ANIMATION_DURATION);
        closeButtonScaleUp.setInterpolator(new AccelerateInterpolator());
        closeButtonScaleUp.start();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
