package com.thebluealliance.androidclient.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentPagerAdapter;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.interfaces.YearsParticipatedUpdate;
import com.thebluealliance.androidclient.modules.SubscriberModule;
import com.thebluealliance.androidclient.modules.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.modules.components.FragmentComponent;
import com.thebluealliance.androidclient.modules.components.HasFragmentComponent;
import com.thebluealliance.androidclient.subscribers.YearsParticipatedDropdownSubscriber;
import com.thebluealliance.androidclient.views.SlidingTabs;

import java.util.Calendar;

import rx.schedulers.Schedulers;

public class ViewTeamActivity extends FABNotificationSettingsActivity implements
  ViewPager.OnPageChangeListener,
  View.OnClickListener,
  HasFragmentComponent,
  YearsParticipatedUpdate {

    public static final String TEAM_KEY = "team_key",
            TEAM_YEAR = "team_year",
            SELECTED_YEAR = "year",
            SELECTED_TAB = "tab";

    private FragmentComponent mComponent;
    private static Object mModule;
    private TextView mWarningMessage;
    private int mCurrentSelectedYearPosition = -1,
            mSelectedTab = -1;

    private String[] mYearsParticipated;

    // Should come in the format frc####
    private String mTeamKey;

    private int mYear;
    private View mYearSelectorContainer;
    private View mYearSelectorSubtitleContainer;
    private TextView mYearSelectorTitle;
    private TextView mYearSelectorSubtitle;
    private ViewPager mPager;
    private ViewTeamFragmentPagerAdapter mAdapter;

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

        setModelKey(mTeamKey, ModelHelper.MODELS.TEAM);
        setContentView(R.layout.activity_view_team);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mYearSelectorContainer = findViewById(R.id.year_selector_container);
        mYearSelectorSubtitleContainer = findViewById(R.id.year_selector_subtitle_container);
        mYearSelectorTitle = (TextView) findViewById(R.id.year_selector_title);
        mYearSelectorSubtitle = (TextView) findViewById(R.id.year_selector_subtitle);

        mWarningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_TAB)) {
                mSelectedTab = savedInstanceState.getInt(SELECTED_TAB);
            }
            if (savedInstanceState.containsKey(SELECTED_YEAR)) {
                mYear = savedInstanceState.getInt(SELECTED_YEAR);
            }
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(TEAM_YEAR)) {
                mYear = getIntent().getIntExtra(TEAM_YEAR, Calendar.getInstance().get(Calendar.YEAR));
            } else {
                mYear = Calendar.getInstance().get(Calendar.YEAR);
            }
            mCurrentSelectedYearPosition = 0;
            mSelectedTab = 0;
        }
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setOffscreenPageLimit(3);
        mPager.setPageMargin(Utilities.getPixelsFromDp(this,
          16));
        // We will notify the fragments of the year later
        mAdapter = new ViewTeamFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey, mYear);
        mPager.setAdapter(mAdapter);

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
        tabs.setOnPageChangeListener(this);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isDrawerOpen()) {
            setupActionBar();
        }
        return super.onPrepareOptionsMenu(menu);
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
            if (mYearsParticipated != null) {

                mYearSelectorSubtitleContainer.setVisibility(View.VISIBLE);

                final Dialog dialog = makeDialogForYearSelection(R.string.select_year,
                                                                 mYearsParticipated);

                mYearSelectorContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.show();
                    }
                });

                if (mCurrentSelectedYearPosition >= 0 && mCurrentSelectedYearPosition < mYearsParticipated.length) {
                    onYearSelected(mCurrentSelectedYearPosition);
                    updateTeamYearSelector(mCurrentSelectedYearPosition);
                } else {
                    onYearSelected(0);
                    updateTeamYearSelector(0);
                }
            }
        }
    }

    private Dialog makeDialogForYearSelection(@StringRes int titleResId, String[] dropdownItems) {
        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(res.getString(titleResId));
        builder.setItems(dropdownItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onYearSelected(which);
            }
        });

        return builder.create();
    }

    private void updateTeamYearSelector(int selectedPosition) {
        if (selectedPosition < 0 || selectedPosition >= mYearsParticipated.length) {
            return;
        }
        mYearSelectorSubtitle.setText(mYearsParticipated[selectedPosition]);
    }

    @Override
    public void updateYearsParticipated(int[] years) {
        String[] dropdownItems = new String[years.length];
        int requestedYearIndex = 0;
        for (int i = 0; i < years.length; i++) {
            if (years[i] == mYear) {
                requestedYearIndex = i;
            }
            dropdownItems[i] = String.valueOf(years[i]);
        }
        mYearsParticipated = dropdownItems;
        onYearSelected(requestedYearIndex);
    }

    private void onYearSelected(int position) {
        // Only handle this if the year has actually changed
        if (position == mCurrentSelectedYearPosition) {
            return;
        }
        mCurrentSelectedYearPosition = position;
        updateTeamYearSelector(position);
        int newYear = Integer.valueOf(mYearsParticipated[mCurrentSelectedYearPosition]);
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
    public void showWarningMessage(String message) {
        mWarningMessage.setText(message);
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
        mSelectedTab = position;
        // hide the FAB if we aren't on the first page
        if (position != 0) {
            hideFab(true);
        } else {
            showFab(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public int getCurrentSelectedYearPosition() {
        return mCurrentSelectedYearPosition;
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

    @Override
    public void inject() {
        getComponent().inject(this);
    }
}
