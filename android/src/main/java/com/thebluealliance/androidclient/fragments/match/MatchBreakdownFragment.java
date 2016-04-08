package com.thebluealliance.androidclient.fragments.match;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.MatchBreakdownBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.MatchBreakdownSubscriber;
import com.thebluealliance.androidclient.views.NoDataView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rx.Observable;

public class MatchBreakdownFragment extends DatafeedFragment<Match, MatchBreakdownBinder.Model,
        MatchBreakdownSubscriber, MatchBreakdownBinder> {

    private static final String KEY = "key";

    private String mMatchKey;

    public static MatchBreakdownFragment newInstance(String matchKey) {
        MatchBreakdownFragment fragment = new MatchBreakdownFragment();
        Bundle data = new Bundle();
        data.putString(KEY, matchKey);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mMatchKey = getArguments().getString(KEY, "");
        }
        if (!MatchHelper.validateMatchKey(mMatchKey)) {
            throw new IllegalArgumentException("Invalid match key " + mMatchKey);
        }
        super.onCreate(savedInstanceState);

        mBinder.setMatchType(MatchHelper.getMatchTypeFromKey(mMatchKey));
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_breakdown, null);
        mBinder.setRootView(view);
        mBinder.setNoDataView((NoDataView) view.findViewById(R.id.no_data));
        return view;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<Match> getObservable(String tbaCacheHeader) {
        // Load data only from local db, MatchInfo fragment will send updated info via EventBus
        return mDatafeed.getCache().fetchMatch(mMatchKey);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("matchBreakdown_%1$s", mMatchKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_poll_black_48dp, R.string.no_match_breakdown);
    }

    @Override
    protected boolean shouldRegisterSubscriberToEventBus() {
        return true;
    }
}
