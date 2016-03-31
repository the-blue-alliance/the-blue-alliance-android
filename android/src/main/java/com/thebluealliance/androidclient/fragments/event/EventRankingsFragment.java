package com.thebluealliance.androidclient.fragments.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.RankingsListSubscriber;

import rx.Observable;

/**
 * Fragment that displays the rankings for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 */
public class EventRankingsFragment extends ListViewFragment<JsonElement, RankingsListSubscriber> {

    private static final String KEY = "eventKey";

    private String mEventKey;

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
        // Reload key if returning from another activity/fragment
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
        super.onCreate(savedInstanceState);
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
    protected Observable<? extends JsonElement> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchEventRankings(mEventKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventRankings_%1$s", mEventKey);
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_poll_black_48dp, R.string.no_ranking_data);
    }
}
