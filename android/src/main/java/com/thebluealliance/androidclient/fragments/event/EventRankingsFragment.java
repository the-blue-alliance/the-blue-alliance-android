package com.thebluealliance.androidclient.fragments.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.fragments.ListviewFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.subscribers.RankingsListSubscriber;

import rx.Observable;

/**
 * Fragment that displays the rankings for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 */
public class EventRankingsFragment extends ListviewFragment<JsonArray, RankingsListSubscriber> {

    private static final String KEY = "mEventKey";
    public static final String DATAFEED_TAG_FORMAT = "event_rankings_%1$s";

    private String mEventKey;
    private String mDatafeedTag;

    /**
     * Creates new rankings fragment for an event
     *
     * @param eventKey the key that represents an FRC event
     * @return new event rankings fragment
     */
    public static EventRankingsFragment newInstance(String eventKey) {
        EventRankingsFragment f = new EventRankingsFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Reload key if returning from another activity/fragment
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
        mDatafeedTag = String.format(DATAFEED_TAG_FORMAT, mEventKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemClickListener((adapterView, view, position, id) -> {
            String teamKey = ((ListElement) ((ListViewAdapter) adapterView.getAdapter())
              .getItem(position)).getKey();
            Intent intent = TeamAtEventActivity.newInstance(getActivity(), mEventKey, teamKey);

             /* Track the call */
            AnalyticsHelper.sendClickUpdate(
              getActivity(), "team@event_click", "EventRankingsFragment", mEventKey);

            startActivity(intent);
        });
        return v;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<JsonArray> getObservable() {
        return mDatafeed.fetchEventRankings(mEventKey);
    }

    @Override
    protected String getDatafeedTag() {
        return mDatafeedTag;
    }
}
