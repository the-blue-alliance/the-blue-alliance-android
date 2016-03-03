package com.thebluealliance.androidclient.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.interfaces.YearsParticipatedUpdate;
import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;
import com.thebluealliance.androidclient.subscribers.YearsParticipatedDropdownSubscriber;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.views.SlidingTabs;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;

public class ViewTeamActivity extends MyTBASettingsActivity implements
        ViewPager.OnPageChangeListener,
        View.OnClickListener,
        HasFragmentComponent,
        YearsParticipatedUpdate {

    public static final String TEAM_KEY = "team_key",
            TEAM_YEAR = "team_year",
            SELECTED_YEAR = "year",
            SELECTED_TAB = "tab";

    private FragmentComponent mComponent;
    private int mCurrentSelectedYearPosition = -1,
            mSelectedTab = -1;

    private int[] mYearsParticipated;

    @Inject TBAStatusController mStatusController;

    // Should come in the format frc####
    private String mTeamKey;
    private int mYear;

    @Bind(R.id.year_selector_container) View mYearSelectorContainer;
    @Bind(R.id.year_selector_subtitle_container) View mYearSelectorSubtitleContainer;
    @Bind(R.id.year_selector_title) TextView mYearSelectorTitle;
    @Bind(R.id.year_selector_subtitle) TextView mYearSelectorSubtitle;
    @Bind(R.id.view_pager) ViewPager mPager;

    ViewTeamFragmentPagerAdapter mAdapter;

    public static Intent newInstance(Context context, String teamKey) {
        System.out.println("making intent for " + teamKey);
        Intent intent = new Intent(context, ViewTeamActivity.class);
        intent.putExtra(TEAM_KEY, teamKey);
        return intent;
    }

    public static Intent newInstance(Context context, String teamKey, int year) {
        Intent intent = new Intent(context, ViewTeamActivity.class);
        intent.putExtra(TEAM_KEY, teamKey);
        intent.putExtra(TEAM_YEAR, year);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTeamKey = getIntent().getStringExtra(TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("ViewTeamActivity must be created with a team key!");
        }

        setModelKey(mTeamKey, ModelType.TEAM);
        setContentView(R.layout.activity_view_team);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_TAB)) {
                mSelectedTab = savedInstanceState.getInt(SELECTED_TAB);
            }
            if (savedInstanceState.containsKey(SELECTED_YEAR)) {
                mYear = savedInstanceState.getInt(SELECTED_YEAR);
            }
        } else {
            int maxYear = mStatusController.getMaxCompYear();
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(TEAM_YEAR)) {
                mYear = getIntent().getIntExtra(TEAM_YEAR, maxYear);
            } else {
                mYear = maxYear;
            }
            mSelectedTab = 0;
        }

        mPager.setOffscreenPageLimit(3);
        mPager.setPageMargin(Utilities.getPixelsFromDp(this, 16));
        // We will notify the fragments of the year later
        mAdapter = new ViewTeamFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey, mYear);
        mPager.setAdapter(mAdapter);

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
        tabs.setOnPageChangeListener(this);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(BaseActivity.WARNING_OFFLINE);
        }

        getComponent().datafeed().fetchTeamYearsParticipated(mTeamKey, null)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new YearsParticipatedDropdownSubscriber(this));

        // We can call this even though the years participated haven't been loaded yet.
        // The years won't be shown yet; this just shows the team number in the toolbar.
        setupActionBar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_YEAR, mYear);
        outState.putInt(SELECTED_TAB, mSelectedTab);
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
        encourageLearning(false);
    }

    private void setupActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
            String teamNumber = mTeamKey.replace("frc", "");
            mYearSelectorTitle.setText(String.format(getString(R.string.team_actionbar_title),
                    teamNumber));

            // If we call this and the years participated haven't been loaded yet, don't try to use them
            if (mYearsParticipated != null && mYearsParticipated.length > 0) {

                mYearSelectorSubtitleContainer.setVisibility(View.VISIBLE);

                final Dialog dialog = makeDialogForYearSelection(R.string.select_year, mYearsParticipated);

                mYearSelectorContainer.setOnClickListener(v -> dialog.show());
            } else {
                // If there are no valid years, hide the subtitle and disable clicking
                mYearSelectorSubtitleContainer.setVisibility(View.GONE);
                mYearSelectorContainer.setOnClickListener(null);
            }
        }
    }

    private Dialog makeDialogForYearSelection(@StringRes int titleResId, int[] dropdownItems) {
        // Create an array of strings from the int years
        String[] years = new String[dropdownItems.length];
        for (int i = 0; i < years.length; i++) {
            years[i] = String.valueOf(dropdownItems[i]);
        }

        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(res.getString(titleResId));
        builder.setItems(years, (dialog, which) -> {
            onYearSelected(which);
        });

        return builder.create();
    }

    private void updateTeamYearSelector(int selectedPosition) {
        if (selectedPosition < 0 || selectedPosition >= mYearsParticipated.length) {
            return;
        }
        mYearSelectorSubtitle.setText(String.valueOf(mYearsParticipated[selectedPosition]));
    }

    @Override
    public void updateYearsParticipated(int[] years) {
        mYearsParticipated = years;

        // If we received a desired year in the intent, find the index of that year if it exists
        int requestedYearIndex = 0;
        for (int i = 0; i < years.length; i++) {
            if (years[i] == mYear) {
                requestedYearIndex = i;
            }
        }

        // Refresh action bar; this will the year subtitle if there are no valid ones
        setupActionBar();

        onYearSelected(requestedYearIndex);
    }

    private void onYearSelected(int position) {
        // Only handle this if the year has actually changed
        if (position == mCurrentSelectedYearPosition) {
            return;
        }

        // Bounds checking!
        if (position < 0 || position >= mYearsParticipated.length) {
            return;
        }

        mCurrentSelectedYearPosition = position;
        updateTeamYearSelector(position);
        int newYear = mYearsParticipated[mCurrentSelectedYearPosition];
        if (newYear == mYear) {
            return;
        }
        mYear = newYear;
        setBeamUri(String.format(NfcUris.URI_TEAM_IN_YEAR, mTeamKey, mYear));
        mAdapter.updateYear(mYear);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isDrawerOpen()) {
                closeDrawer();
                return true;
            }

            // If this tasks exists in the back stack, it will be brought to the front and all other activities
            // will be destroyed. HomeActivity will be delivered this intent via onNewIntent().
            startActivity(HomeActivity.newInstance(this, R.id.nav_item_teams).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mSelectedTab = position;

        switch (position) {
            case ViewTeamFragmentPagerAdapter.TAB_INFO:
                setupFabForMyTbaSettingsTab();
                showFab(true, false);
                break;
            case ViewTeamFragmentPagerAdapter.TAB_MEDIA:
                showFab(true, true);
                setFabColor(R.color.accent);
                setFabDrawable(R.drawable.ic_add_a_photo_white_24dp);
                break;
            default:
                hideFab(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected boolean onFabClick() {
        switch (mSelectedTab) {
            case ViewTeamFragmentPagerAdapter.TAB_MEDIA:
                Toast.makeText(this, "Upload image!", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
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
