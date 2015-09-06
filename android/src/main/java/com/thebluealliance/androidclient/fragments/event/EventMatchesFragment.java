package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.binders.ExpandableListViewBinder;
import com.thebluealliance.androidclient.binders.MatchListBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.MatchListSubscriber;
import com.thebluealliance.androidclient.views.ExpandableListView;
import com.thebluealliance.androidclient.views.NoDataView;

import java.util.List;

import rx.Observable;

public class EventMatchesFragment
  extends DatafeedFragment<List<Match>, List<ListGroup>, MatchListSubscriber, MatchListBinder> {

    private static final String KEY = "eventKey", TEAM = "teamKey";

    private String mEventKey, mTeamKey;
    private Parcelable mListState;
    private MatchListAdapter mAdapter;
    private ExpandableListView mListView;
    private int mFirstVisiblePosition;

    public static EventMatchesFragment newInstance(String eventKey, String teamKey) {
        EventMatchesFragment f = new EventMatchesFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        data.putString(TEAM, teamKey);
        f.setArguments(data);
        return f;
    }

    public static EventMatchesFragment newInstance(String eventKey) {
        return newInstance(eventKey, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
            mTeamKey = getArguments().getString(TEAM, "");
        }
        super.onCreate(savedInstanceState);

        mSubscriber.setEventKey(mEventKey);
        mSubscriber.setTeamKey(mTeamKey);
        mBinder.setExpandMode(ExpandableListViewBinder.MODE_EXPAND_ONLY);
        mBinder.setSelectedTeam(mTeamKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.expandable_list_view_with_spinner, null);
        mListView = (ExpandableListView) v.findViewById(R.id.expandable_list);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);
        mBinder.expandableListView = mListView;
        mBinder.progressBar = progressBar;
        mBinder.setNoDataView((NoDataView) v.findViewById(R.id.no_data));

        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mListView.setSelection(mFirstVisiblePosition);
            progressBar.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListView != null) {
            mAdapter = (MatchListAdapter) mListView.getExpandableListAdapter();
            mListState = mListView.onSaveInstanceState();
            mFirstVisiblePosition = mListView.getFirstVisiblePosition();
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Match>> getObservable(String tbaCacheHeader) {
        if (mTeamKey == null || mTeamKey.isEmpty()) {
            return mDatafeed.fetchEventMatches(mEventKey, tbaCacheHeader);
        } else {
            return mDatafeed.fetchTeamAtEventMatches(mTeamKey, mEventKey, tbaCacheHeader);
        }
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventMatches_%1$s_%2$s", mEventKey, mTeamKey);
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_gamepad_variant_black_48dp, R.string.no_match_data);
    }
}
