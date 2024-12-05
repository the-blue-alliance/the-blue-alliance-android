package com.thebluealliance.androidclient.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.ShareUris;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.TeamAtEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.databinding.ActivityTeamAtEventBinding;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.eventbus.TeamAvatarUpdateEvent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.interfaces.EventsParticipatedUpdate;
import com.thebluealliance.androidclient.models.ApiStatus;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.EventsParticipatedDropdownSubscriber;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.views.SlidingTabs;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import rx.schedulers.Schedulers;

@AndroidEntryPoint
public class TeamAtEventActivity extends MyTBASettingsActivity
  implements ViewPager.OnPageChangeListener, EventsParticipatedUpdate {

    public static final String EVENT = "eventKey";
    public static final String TEAM = "teamKey";

    private String mEventKey, mTeamKey;
    private TeamAtEventFragmentPagerAdapter mAdapter;

    private int mCurrentSelectedEventPosition = -1;
    private ImmutableList<Event> mEventsParticipated;

    private ActivityTeamAtEventBinding mBinding;

    @Inject CacheableDatafeed mDatafeed;

    public static Intent newInstance(Context c, String eventTeamKey) {
        return newInstance(c, EventTeamHelper.getEventKey(eventTeamKey), EventTeamHelper.getTeamKey(eventTeamKey));
    }

    public static Intent newInstance(Context c, String eventKey, String teamKey) {
        Intent intent = new Intent(c, TeamAtEventActivity.class);
        intent.putExtra(EVENT, eventKey);
        intent.putExtra(TEAM, teamKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityTeamAtEventBinding.inflate(getLayoutInflater(), mRootView, true);

        Bundle extras = getIntent().getExtras();
        if (extras != null && (extras.containsKey(EVENT) && extras.containsKey(TEAM))) {
            mTeamKey = extras.getString(TEAM);
            mEventKey = extras.getString(EVENT);
        } else {
            throw new IllegalArgumentException("TeamAtEventActivity must be constructed with event and team parameters");
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EVENT)) {
                mEventKey = savedInstanceState.getString(EVENT);
            }
        }

        String eventTeamKey = EventTeamHelper.generateKey(mEventKey, mTeamKey);
        setModelKey(eventTeamKey, ModelType.EVENTTEAM);
        setShareEnabled(true);

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new TeamAtEventFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey, mEventKey);
        pager.setAdapter(mAdapter);
        // To support refreshing, all pages must be held in memory at once
        // This should be increased if we ever add more pages
        pager.setOffscreenPageLimit(6);
        pager.setPageMargin(Utilities.getPixelsFromDp(this, 16));

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setOnPageChangeListener(this);
        tabs.setViewPager(pager);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        setSupportActionBar(findViewById(R.id.toolbar));
        setupActionBar();

        int year = EventHelper.getYear(mEventKey);
        mDatafeed.fetchTeamEvents(mTeamKey, year, null)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new EventsParticipatedDropdownSubscriber(this));

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(BaseActivity.WARNING_OFFLINE);
        }

        setShareUri(String.format(
                ShareUris.URI_TEAM_AT_EVENT,
                TeamHelper.getTeamNumber(mTeamKey),
                EventHelper.getYear(mEventKey),
                mEventKey));

        setSettingsToolbarTitle("Team at Event Settings");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.team_at_event, menu);
        getMenuInflater().inflate(R.menu.stats_help_menu, menu);
        mOptionsMenu = menu;
        mOptionsMenu.findItem(R.id.stats_help).setVisible(false);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EVENT, mEventKey);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_event:
                startActivity(ViewEventActivity.newInstance(this, mEventKey));
                return true;
            case R.id.action_view_team:
                int year = Integer.parseInt(mEventKey.substring(0, 4));
                startActivity(ViewTeamActivity.newInstance(this, mTeamKey, year));
                return true;
            case R.id.stats_help:
                Utilities.showHelpDialog(this, R.raw.stats_help, getString(R.string.stats_help_title));
                return true;
            case android.R.id.home:
                if (isDrawerOpen()) {
                    closeDrawer();
                    return true;
                }
                Intent upIntent = ViewEventActivity.newInstance(this, mEventKey);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, R.id.nav_item_teams))
                            .addNextIntent(ViewEventActivity.newInstance(this, mEventKey)).startActivities();
                } else {
                    TbaLogger.d("Navigating up...");
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(false);

            String teamNumber = mTeamKey.replace("frc", "");
            mBinding.eventSelectorTitle.setText(getString(R.string.team_actionbar_title, teamNumber));

            if (mEventsParticipated != null && mEventsParticipated.size() > 0) {
                mBinding.eventSelectorSubtitle.setVisibility(View.VISIBLE);
                final Dialog dialog = makeDialogForEventSelection(R.string.select_event, mEventsParticipated);
                mBinding.eventSelectorContainer.setOnClickListener(v -> dialog.show());
            } else {
                mBinding.eventSelectorSubtitleContainer.setVisibility(View.GONE);
                mBinding.eventSelectorContainer.setOnClickListener(null);
            }
        }
    }

    private Dialog makeDialogForEventSelection(@StringRes int titleResId, ImmutableList<Event> eventsParticipated) {
        String[] events = new String[eventsParticipated.size()];
        for (int i = 0; i < eventsParticipated.size(); i++) {
            Event event = eventsParticipated.get(i);
            events[i] = event.getYear() + " " + event.getShortName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(titleResId));
        builder.setItems(events, (dialog, which) -> onEventSelected(which));
        return builder.create();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void updateEventsParticipated(List<Event> events) {
        mEventsParticipated = ImmutableList.copyOf(events);

        int requestedEventIndex = -1;
        for (int i = 0; i < mEventsParticipated.size(); i++) {
            if (mEventsParticipated.get(i).getKey().equals(mEventKey)) {
                requestedEventIndex = i;
            }
        }

        setupActionBar();
        onEventSelected(requestedEventIndex);
    }

    private void onEventSelected(int position) {
        if (position == mCurrentSelectedEventPosition) {
            return;
        }

        if (position < 0 || position >= mEventsParticipated.size()) {
            return;
        }

        mCurrentSelectedEventPosition = position;
        updateEventSelector(mCurrentSelectedEventPosition);
        Event newEvent = mEventsParticipated.get(mCurrentSelectedEventPosition);
        if (mEventKey.equals(newEvent.getKey())) {
            return;
        }

        mEventKey = newEvent.getKey();
        mAdapter.updateEvent(mEventKey);
        mAdapter.notifyDataSetChanged();

        String eventTeamKey = EventTeamHelper.generateKey(mEventKey, mTeamKey);
        setModelKey(eventTeamKey, ModelType.EVENTTEAM);
        setShareUri(String.format(
                ShareUris.URI_TEAM_AT_EVENT,
                TeamHelper.getTeamNumber(mTeamKey),
                EventHelper.getYear(mEventKey),
                mEventKey));
    }

    private void updateEventSelector(int selectedPosition) {
        if (selectedPosition < 0 || selectedPosition >= mEventsParticipated.size()) {
            return;
        }

        Event selectedEvent = mEventsParticipated.get(selectedPosition);
        mBinding.eventSelectorSubtitle.setText(getString(R.string.team_at_event_actionbar_subtitle,
                selectedEvent.getYear(), selectedEvent.getShortName()));
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateTeamAvatar(TeamAvatarUpdateEvent avatarUpdateEvent) {
        if (avatarUpdateEvent == null
                || avatarUpdateEvent.getB64Image() == null
                || avatarUpdateEvent.getB64Image().isEmpty()) {
            mBinding.teamAvatar.setVisibility(View.GONE);
        } else {
            byte[] bytes = Base64.decode(avatarUpdateEvent.getB64Image(), Base64.DEFAULT);
            Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mBinding.teamAvatar.setImageBitmap(Bitmap.createScaledBitmap(avatar, 80, 80, false));
            mBinding.teamAvatar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onTbaStatusUpdate(ApiStatus newStatus) {
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
    public void onPageSelected(int position) {
        if (mOptionsMenu != null) {
            if (position == Arrays.binarySearch(mAdapter.TITLES, "Stats")) {
                //stats position
                mOptionsMenu.findItem(R.id.stats_help).setVisible(true);
            } else {
                mOptionsMenu.findItem(R.id.stats_help).setVisible(false);
            }
        }

        // hide the FAB if we aren't on the first page
        if (position != 0) {
            hideFab(true);
        } else {
            syncFabVisibilityWithMyTbaEnabled(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
