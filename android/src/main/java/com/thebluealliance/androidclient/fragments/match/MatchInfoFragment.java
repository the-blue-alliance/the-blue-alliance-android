package com.thebluealliance.androidclient.fragments.match;

import android.os.Bundle;

import com.thebluealliance.androidclient.fragments.ListviewFragment;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.subscribers.MatchInfoSubscriber;

import rx.Observable;

public class MatchInfoFragment extends ListviewFragment<Match, MatchInfoSubscriber> {

    private static final String KEY = "key";
    public static final String DATAFEED_TAG_FORMAT = "match_info_%1$s";

    private String mMatchKey;
    private String mEventKey;
    private String mDatafeedTag;
    private String mExtraTags[];

    public static MatchInfoFragment newInstance(String matchKey) {
        MatchInfoFragment f = new MatchInfoFragment();
        Bundle data = new Bundle();
        data.putString(KEY, matchKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMatchKey = getArguments().getString(KEY, "");
        }
        if (!MatchHelper.validateMatchKey(mMatchKey)) {
            throw new IllegalArgumentException("Invalid match key " + mMatchKey);
        }
        mEventKey = MatchHelper.getEventKeyFromMatchKey(mMatchKey);
        mDatafeedTag = String.format(DATAFEED_TAG_FORMAT, mMatchKey);
        mExtraTags = new String[]{String.format(EventInfoFragment.DATAFEED_TAG_FORMAT, mEventKey)};
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<Match> getObservable() {
        return mDatafeed.fetchMatch(mMatchKey);
    }

    @Override
    protected Observable[] getExtraObservables() {
        return new Observable[]{mDatafeed.fetchEvent(mEventKey)};
    }

    @Override
    protected String getDatafeedTag() {
        return mDatafeedTag;
    }
}
