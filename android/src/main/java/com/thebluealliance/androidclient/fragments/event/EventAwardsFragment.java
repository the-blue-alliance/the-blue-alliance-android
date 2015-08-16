package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.AwardsListSubscriber;

import java.util.List;

import rx.Observable;

public class EventAwardsFragment extends ListViewFragment<List<Award>, AwardsListSubscriber> {
    private static final String EVENT_KEY = "eventKey", TEAM_KEY = "teamKey";

    private String mEventKey, mTeamKey;

    public static EventAwardsFragment newInstance(String eventKey) {
        EventAwardsFragment f = new EventAwardsFragment();
        Bundle data = new Bundle();
        data.putString(EVENT_KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    public static EventAwardsFragment newInstance(String eventKey, String teamKey) {
        EventAwardsFragment f = new EventAwardsFragment();
        Bundle data = new Bundle();
        data.putString(EVENT_KEY, eventKey);
        data.putString(TEAM_KEY, teamKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mEventKey = getArguments().getString(EVENT_KEY, "");
            mTeamKey = getArguments().getString(TEAM_KEY, "");
        }
        super.onCreate(savedInstanceState);

        mSubscriber.setTeamKey(mTeamKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        //disable touch feedback (you can't click the elements here...)
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setSelector(R.drawable.transparent);
        return view;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Award>> getObservable() {
        if (mTeamKey == null || mTeamKey.isEmpty()) {
            return mDatafeed.fetchEventAwards(mEventKey);
        } else {
            return mDatafeed.fetchTeamAtEventAwards(mTeamKey, mEventKey);
        }
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_trophy_black_48dp, R.string.no_awards_data);
    }
}
