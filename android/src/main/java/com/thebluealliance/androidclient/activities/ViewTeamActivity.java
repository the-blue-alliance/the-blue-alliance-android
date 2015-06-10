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
import com.thebluealliance.androidclient.TBAAndroidModule;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentPagerAdapter;
import com.thebluealliance.androidclient.background.team.MakeActionBarDropdownForTeam;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.RefreshManager;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.views.SlidingTabs;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.Module;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                ViewTeamActivity.class
        },
        addsTo = TBAAndroidModule.class,
        library = true
)
public class ViewTeamActivity extends FABNotificationSettingsActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public static final String TEAM_KEY = "team_key",
            TEAM_YEAR = "team_year",
            SELECTED_YEAR = "year",
            SELECTED_TAB = "tab";

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

        // disable legacy RefreshableHostActivity
        setRefreshEnabled(false);

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

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setOffscreenPageLimit(3);
        pager.setPageMargin(Utilities.getPixelsFromDp(this,
                                                      16));
        // We will notify the fragments of the year later
        final ViewTeamFragmentPagerAdapter adapter = new ViewTeamFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey);
        pager.setAdapter(adapter);

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(this);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }

        new MakeActionBarDropdownForTeam(this).execute(mTeamKey);

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
        mYearSelectorSubtitle.setText(mYearsParticipated[selectedPosition]);
    }

    public void onYearsParticipatedLoaded(int[] years) {
        String[] dropdownItems = new String[years.length];
        int requestedYearIndex = 0;
        for (int i = 0; i < years.length; i++) {
            if (years[i] == mYear) {
                requestedYearIndex = i;
            }
            dropdownItems[i] = String.valueOf(years[i]);
        }
        mYearsParticipated = dropdownItems;
        mCurrentSelectedYearPosition = requestedYearIndex;

        setupActionBar();

        // Notify anyone that cares that the year changed
        EventBus.getDefault().post(new YearChangedEvent(Integer.parseInt(mYearsParticipated[mCurrentSelectedYearPosition])));
    }

    private void onYearSelected(int position) {
        // Only handle this if the year has actually changed
        if (position == mCurrentSelectedYearPosition) {
            return;
        }
        mCurrentSelectedYearPosition = position;
        mYear = Integer.valueOf(mYearsParticipated[mCurrentSelectedYearPosition]);

        updateTeamYearSelector(position);

        EventBus.getDefault().post(new YearChangedEvent(mYear));

        setBeamUri(String.format(NfcUris.URI_TEAM_IN_YEAR, mTeamKey, mYear));
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
}
