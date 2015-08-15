package com.thebluealliance.androidclient.fragments.match;

import android.os.Bundle;

import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.subscribers.MatchInfoSubscriber;

import rx.Observable;

public class MatchInfoFragment extends ListViewFragment<Match, MatchInfoSubscriber> {

    private static final String KEY = "key";

    private String mMatchKey;
    private String mEventKey;

    public static MatchInfoFragment newInstance(String matchKey) {
        MatchInfoFragment f = new MatchInfoFragment();
        Bundle data = new Bundle();
        data.putString(KEY, matchKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mMatchKey = getArguments().getString(KEY, "");
        }
        if (!MatchHelper.validateMatchKey(mMatchKey)) {
            throw new IllegalArgumentException("Invalid match key " + mMatchKey);
        }
        mEventKey = MatchHelper.getEventKeyFromMatchKey(mMatchKey);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<Match> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchMatch(mMatchKey, tbaCacheHeader);
    }

    @Override
    protected Observable[] getExtraObservables(String tbaCacheHeader) {
        return new Observable[]{mDatafeed.fetchEvent(mEventKey, tbaCacheHeader)};
    }
}
